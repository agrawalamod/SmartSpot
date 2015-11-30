package com.example.agrawalamod.smartspot;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GeofenceActivity extends AppCompatActivity
        implements ResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GeofenceResultReceiver.Receiver
{

    ///for testing
    private List mGeofenceList = new ArrayList();
    private PendingIntent mGeofencePendingIntent;
    Activity curr_activty;



    private String geofenceName = "Work";
    private double x = 28.584930;
    private double y = 77.056711;
    private int radius = 1000;


    private String TAG = "MainActivity";
    private String CityName="";
    private String StateName="";
    private String CountryName="";

    private LocationManager locManager;
    private LocationListener locListener;
    private Location location; //contains longitude and latitude of present location

    private TextView textView2, textView3, textView1;
    private Button button1;

    protected GoogleApiClient mGoogleApiClient;

    private static final String SSID = "SSID";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String PASSWORD = "PASSWORD";
    private static final String RADIUS = "RADIUS";
    private static final String DATA = "DATA";

    private Set<String> SSIDlist = new HashSet<String>();
    private Iterator<String> i;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private HashMap<String, ArrayList<String>> SSIDInfo = new HashMap(); //0->lat 1->long 2->password 3->radius 4->data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MAIN ACTIVITY Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);
        sharedPref = this.getSharedPreferences("geofence", MODE_APPEND);
        SSIDlist = null;
        curr_activty = this;

        SSIDlist = sharedPref.getStringSet(SSID, SSIDlist);


        Log.i(TAG, "SSID LIST is " + SSIDlist);
        ArrayList<String> data=null;
        if(SSIDlist!=null)
        {
            i = SSIDlist.iterator();
            data = new ArrayList();
            while(i.hasNext())
            {
                data.add(i.next());
            }
            Log.i(TAG, "Size of Data " + String.valueOf(data.size()));
            final ListView listview = (ListView) findViewById(R.id.textView);
            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, data);
            listview.setAdapter(adapter);
        }


        mGoogleApiClient=null;

        buildGoogleApiClient();
        startMonitoring();

        if(data == null)
        {
            Log.i(TAG, "SSIDList is null");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle("New Hotspot?");

            alertDialogBuilder
                    .setMessage("Create first Hotspot??")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity

                            Toast.makeText(getApplicationContext(), "Open Create Hotspot Page here", Toast.LENGTH_SHORT).show();
                            startCreateGeofence();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            Toast.makeText(getApplicationContext(), "Do nothing", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if(SSIDlist!=null)
        {
            i = SSIDlist.iterator();
            while(i.hasNext())
            {
                String key = i.next();
                ArrayList<String> a = new ArrayList();
                Log.i(TAG, "Lat : "+ key+ LATITUDE);
                a.add(sharedPref.getString(key + LATITUDE, new String()));
                a.add(sharedPref.getString(key + LONGITUDE, new String()));
                a.add(sharedPref.getString(key+ PASSWORD, new String()));
                a.add(sharedPref.getString(key+ RADIUS, new String()));
                a.add(sharedPref.getString(key+ DATA, new String()));
                SSIDInfo.put(key, a);

            }
        }


        button1 = (Button) findViewById(R.id.monitor);


        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //startMonitoring(); //create location service and get present location coordinates
                if(location!=null)
                {
                    String HotspotSSID = checkRadius();
                    if(HotspotSSID==null)
                    {
                        startCreateGeofence();
                    }
                    else
                    {
                        int r = Integer.valueOf(SSIDInfo.get(HotspotSSID).get(3));
                        createGeofence(location, r);
                        addGeofences();
                        mGeofencePendingIntent = getGeofencePendingIntent();


                        //start hotspot creation activity
                        //fire the following intent by adding the class name
                        //as soon as you start the hotspot, start geofence for the following the SSID
                        //using the geofence, you shall monitor for exit(already done for the mid sems)

                      Intent i = new Intent(GeofenceActivity.this, Activity4.class);
                      i.putExtra("Longitude", location.getLongitude());
                      i.putExtra("Latitude", location.getLatitude());
                      i.putExtra("Radius", SSIDInfo.get(HotspotSSID).get(3));
                      i.putExtra("Password", SSIDInfo.get(HotspotSSID).get(2));
                        i.putExtra("MaxData", SSIDInfo.get(HotspotSSID).get(4));
                      i.putExtra("SSID", HotspotSSID);
                        Toast.makeText(getApplicationContext(), "Start Hotspot " + HotspotSSID, Toast.LENGTH_SHORT).show();
                        startActivity(i);

                    }
                }

            }

        });




    }

    /*private void startHotspot() {
        WifiApManager wifiApManager = new WifiApManager(this);
        if (wifiApManager.isWifiApEnabled()) {
            Log.i(TAG, "it wors");
            button1.performClick();
            }
        }*/


    private void startCreateGeofence()
    {
        Intent intent = new Intent(this, CreateGeofence.class);
        // startMonitoring();
        Log.i(TAG, "start Activity");

        if(location.getLongitude()!=0 && location.getLatitude()!=0)
        {
            intent.putExtra("Longitude", location.getLongitude());
            intent.putExtra("Latitude", location.getLatitude());
            this.startActivity(intent);
        }
    }

    private void startMonitoring()
    {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener); //for this I had to degrade the target class to 19
        if (mGoogleApiClient != null) {

            mGoogleApiClient.connect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i(TAG, "GoogleClient Built");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startHotspot();

        Log.i(TAG, "onStart");


    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        getMyCurrentLocation(location);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void getMyCurrentLocation(Location l) {

        Log.i(TAG, "Entered get my Current Location");
        Double MyLong=0.0, MyLat=0.0;

        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) //this means GPS is not available
        {
            Log.i(TAG, "GPS Disabled");
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
        }

        if (l != null) {

            Log.i(TAG, "present Location got!");
            MyLat = l.getLatitude();
            MyLong = l.getLongitude();


        } else {
            Location loc= getLastKnownLocation(this);
            if (loc != null) {
                Log.i(TAG, "Getting Last Location");
                MyLat = loc.getLatitude();
                MyLong = loc.getLongitude();


            }
        }
        locManager.removeUpdates(locListener); // removes the periodic updates from location listener to //avoid battery drainage. If you want to get location at the periodic intervals call this method using //pending intent.

        try
        {
            // Getting address from found locations.
            ///redundant
            Geocoder geocoder;

            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

            StateName= addresses.get(0).getAdminArea();
            CityName = addresses.get(0).getLocality();
            CountryName = addresses.get(0).getCountryName();
            // you can get more details other than this . like country code, state code, etc.


            /*System.out.println(" StateName " + StateName);
            System.out.println(" CityName " + CityName);
            System.out.println(" CountryName " + CountryName);*/
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            //textView1.setText(" StateName " + StateName + " CityName " + CityName + " CountryName " + CountryName + "Locality " + addresses.get(0).getLocality());
           /* textView1.setText("Address " + address + "\ncity " + city
                                + "\nState " + state + "\nCountry " + country
                                + "\npostalCode " + postalCode + "\nknownName " + knownName);*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*textView2.setText(""+MyLat);
        textView3.setText(""+MyLong);*/
        location.setLatitude(MyLat);
        location.setLongitude(MyLong);
        //textView1.setText("Distance from IIITD" + location.distanceTo(IIITD)/1000);

    }

    public String checkRadius()
    {
        Location l = new Location("currentLocation");
        if(SSIDlist!=null)
        {
            for(String key:SSIDInfo.keySet())
            {
                Log.i(TAG, "Lat" + (SSIDInfo.get(key).get(0)));

                l.setLatitude(Double.parseDouble(SSIDInfo.get(key).get(0)));
                l.setLongitude(Double.parseDouble(SSIDInfo.get(key).get(1)));
                Double radius = Double.parseDouble(SSIDInfo.get(key).get(3));

                if(l.distanceTo(location) <= radius)
                {
                    return key;
                }
            }
        }

        return null;
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

    private Location getLastKnownLocation(Context context)
    {
        Location location = null;
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do
        {
            //System.out.println("---------------------------------------------------------------------");
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            //if(i != 0 && !locationmanager.isProviderEnabled(s))
            if(i != false && !locationmanager.isProviderEnabled(s))
                continue;
            // System.out.println("provider ===> "+s);
            Location location1 = locationmanager.getLastKnownLocation(s);
            if(location1 == null)
                continue;
            if(location != null)
            {
                //System.out.println("location ===> "+location);
                //System.out.println("location1 ===> "+location);
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if(f >= f1)
                {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if(l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
            // System.out.println("location  out ===> "+location);
            //System.out.println("location1 out===> "+location);
            i = locationmanager.isProviderEnabled(s);
            // System.out.println("---------------------------------------------------------------------");
        } while(true);
        return location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }
    public void createGeofence(Location location, Integer radius)
    {
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")

                .setCircularRegion(
                        location.getLatitude(),
                        location.getLongitude(),
                        radius)
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
            //Toast.makeText(getApplicaIIIT-Delhi's Academic Block  Academic Block Geofence Added.", Toast.LENGTH_SHORT).show();
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
        //intent.putExtra("email",email);
        //intent.putExtra("GeofenceResult", GeofenceResult);
        System.out.println("Service started");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }

    @Override
    public void onResult(Result result) {

    }
}

