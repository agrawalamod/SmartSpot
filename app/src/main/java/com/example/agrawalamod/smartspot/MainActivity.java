package com.example.agrawalamod.smartspot;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, ResultCallback<Status> {

    private GoogleApiClient mGoogleApiClient;

    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    public static final String extra="EXTRA";

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button2) {
            //Toast.makeText(getApplicationContext(), "Reaches Here", Toast.LENGTH_LONG).show();
            onSignOutClicked();
        }

        if (v.getId() == R.id.button) {
            onSignInClicked();
        }
    }

    private boolean mIsResolving = false;
    public static final String TAG = "MainActivity";
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<Geofence>();
        //buildGoogleApiClient();
        populateGeofenceList();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void populateGeofenceList() {

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("IIITD")
                .setCircularRegion(
                        28.544487,
                        77.272619,
                        100000
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Log.i(TAG, "Opening GeofenceActivity");
        Intent intent = new Intent(this, Activity4.class);
        email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        intent.putExtra(TAG, email);
        startActivity(intent);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "not_connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        if(!email.toLowerCase().contains("@iiitd.ac.in")) {
            Toast.makeText(getApplicationContext(), "Signed In: " + email, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Signed In");
            addGeofences();
            Log.i("MainActivity", "onConnect");
        }
        else {
            onSignOutClicked();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();

        Toast.makeText(getApplicationContext(), "Signing In", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Signing in");
    }

    private void onSignOutClicked() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();

            Toast.makeText(getApplicationContext(), "Signing Out", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Signing out");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }

            else {

                Log.d(TAG, "Could not resolve connection ");
            }
        }

        else {

            Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Signed out");
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e("MainActivity", "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResult(Status status) {

    }

}