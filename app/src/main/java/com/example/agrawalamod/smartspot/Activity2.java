package com.example.agrawalamod.smartspot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class Activity2 extends AppCompatActivity implements
        ResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GeofenceResultReceiver.Receiver
{

    private GoogleApiClient mGoogleApiClient;
    private List mGeofenceList = new ArrayList();
    private PendingIntent mGeofencePendingIntent;
    private String TAG;
    private Location mLastLocation;
    private static final int RC_SIGN_IN = 0;
    private String email;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    TextView location;
    TextView locationResult;
    GeofenceResultReceiver GeofenceResult;
    Button cont;
    Activity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        email = getIntent().getStringExtra("email");
        location = (TextView) findViewById(R.id.textView12);
        locationResult = (TextView) findViewById(R.id.textView13);
        cont = (Button) findViewById(R.id.button7);

        cont.setOnClickListener(myhandler);
        GeofenceResult = new GeofenceResultReceiver(new Handler());
        GeofenceResult.setReceiver(this);
        activity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }
    @Override
    protected void onStart() {
        super.onStart();
        mShouldResolve = true;
        mGoogleApiClient.connect();
        System.out.println("OnStart");

    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        System.out.println("OnStop");
    }
    protected void onDestroy()
    {
        super.onDestroy();
        System.out.println("OnDestroy");
    }


    public void createGeofence()
    {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")

                .setCircularRegion(
                        28.5444498,
                        77.2726199,
                        1000)
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
        System.out.println("Geofence created");
    }
    private void addGeofences()
    {
        if(mGoogleApiClient.isConnected())
        {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);

            System.out.println("Geofence added");
            Toast.makeText(getApplicationContext(), "IIIT-Delhi's Academic Block Geofence Added.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            System.out.println("API not connected");
            Toast.makeText(getApplicationContext(), "Couldn't connect to Google Play Services. Check Internet.", Toast.LENGTH_LONG).show();

        }
    }
    private void stopGeofences()
    {
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }



    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra("email",email);
        //intent.putExtra("GeofenceResult", GeofenceResult);
        System.out.println("Service started");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(Result result)
    {
        System.out.println("Reached OnResult");

    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Google Play Services connected!", Toast.LENGTH_SHORT).show();
        mShouldResolve = false;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        System.out.println("API Location Services Connected!");
        System.out.print("Last Location: ");
        Double Lat = mLastLocation.getLatitude();
        Double Long = mLastLocation.getLongitude();
        location.setText(Lat.toString() + ", " + Long.toString());
        System.out.print(mLastLocation.getLatitude());
        System.out.print(", ");
        System.out.println(mLastLocation.getLongitude());

        createGeofence();
        addGeofences();

        mGeofencePendingIntent = getGeofencePendingIntent();
    }


    @Override
    public void onConnectionSuspended(int i)
    {
        System.out.println("API Location Services Suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        System.out.println("API Location Services Connection Failed");
        Toast.makeText(getApplicationContext(), "Couldn't connect to Google Play Services. Check Internet.", Toast.LENGTH_LONG).show();
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
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                System.out.println("API Location Services Couldn't Connect");
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
            System.out.println("API Location Services Disconnected");
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }
    View.OnClickListener myhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent launchActivity4 = new Intent(activity, Activity4.class);
            startActivity(launchActivity4);



        }
    };

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        System.out.println("onReceive Result");

        if(resultData.getBoolean("isInside")==true)
        {
            locationResult.setText("You were found inside Geofence");
        }
        else if(resultData.getBoolean("isInside") == false)
        {
            locationResult.setText("You were found outside Geofence");

        }

    }
}
