package com.example.agrawalamod.smartspot;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{
    private Person currentPerson;
    private String personName;
    private String personPhoto;
    private String personGooglePlusProfile;
    private String email;



    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);


    }
    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        System.out.println("Signing in");
        //mStatus.setText(R.string.signing_in);
    }
    @Override
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        System.out.println("OnStart");

    }


    @Override
    protected void onStop() {
        onSignOutClicked();
        super.onStop();
        mGoogleApiClient.disconnect();
        System.out.println("OnStop");
    }
    protected void onDestroy()
    {
        super.onDestroy();
        System.out.println("OnDestroy");
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("Sign In: On Connected");
        mShouldResolve = false;

        // Show the signed-in UI

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null)
        {
            currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            personName = currentPerson.getDisplayName();
            personPhoto = currentPerson.getImage().getUrl();
            personGooglePlusProfile = currentPerson.getUrl();
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String iiitd = new String();
            boolean isIIITD = contains(email, "@iiitd.ac.in");
            System.out.println(email);
            if(isIIITD)
            {
                System.out.println("Starting Activity 2");
                Intent intent = new Intent(this, Activity2.class);
                //intent.putExtra("email",email);
                startActivity(intent);
            }
            else
            {
                onSignOutClicked();
                System.out.print("Not IIITD ID");
                Toast.makeText(getApplicationContext(), "Couldn't Sign In. This is not a IIIT-Delhi Account.", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            System.out.println("This is null!!!");
        }



        //showSignedInUI();

    }
    public boolean contains( String haystack, String needle ) {
        haystack = haystack == null ? "" : haystack;
        needle = needle == null ? "" : needle;

        // Works, but is not the best.
        //return haystack.toLowerCase().indexOf( needle.toLowerCase() ) > -1

        return haystack.toLowerCase().contains( needle.toLowerCase() );
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Sign in: suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Toast.makeText(getApplicationContext(), "Couldn't Connect. Check Internet Connection.", Toast.LENGTH_LONG).show();
        //System.out.println("Couldn't connect to Google");
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
                System.out.println("Couldn't Sign IN");
                //Toast.makeText(getApplicationContext(), "Couldn't Connect. Check Internet Connection.", Toast.LENGTH_LONG).show();

                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
            System.out.println("Signed out");
        }

    }
    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        //showSignedOutUI();
        System.out.println("Sign Out Clicked");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            onSignInClicked();
        }
        else if(v.getId() == R.id.button2) {
            onSignOutClicked();
        }

    }
}
