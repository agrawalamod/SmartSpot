package com.example.agrawalamod.smartspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by protichi on 22-Nov-15.
 */
public class CreateGeofence extends AppCompatActivity
{
    private static final String SSID = "SSID";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String PASSWORD = "PASSWORD";
    private static final String RADIUS = "RADIUS";
    private static final String DATA = "DATA";

    private EditText ssid, password, confirmPassword, radius;
    private TextView coordinates;
    private Button save;
    private TextView maxData;
    private String maxDataValue;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    Set<String> SSIDlist = new HashSet<String>();

    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_geofence);
        sharedPref = this.getSharedPreferences("geofence", Context.MODE_APPEND);
        editor = sharedPref.edit();
        SSIDlist = sharedPref.getStringSet(SSID, SSIDlist);



        ssid = (EditText) findViewById(R.id.ssid);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        radius = (EditText) findViewById(R.id.radius);
        coordinates = (TextView) findViewById(R.id.coordinates);
        save = (Button) findViewById(R.id.save);
        maxData = (TextView) findViewById(R.id.maxDataLimit);

        Double currentLocationLatitude = getIntent().getExtras().getDouble("Latitude");
        Double currentLocationLongitude = getIntent().getExtras().getDouble("Longitude");
        coordinates.setText("Latitude: " + currentLocationLatitude + " Longitude: " + currentLocationLongitude);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if(ssid.getText().toString().length()== 0 || confirmPassword.getText().toString().length()==0 || radius.getText().toString().length()==0||maxData.getText().toString().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "Fill all the values", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Password not matching", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                    if(password.getText().toString().length()<8 && confirmPassword.getText().toString().length() < 8)
                    {
                        Toast.makeText(getApplicationContext(), "Passkey should be at least 8 characters", Toast.LENGTH_SHORT).show();
                        flag = false;

                    }
                    if(flag == true)
                    {

                        Double currentLocationLatitude = getIntent().getExtras().getDouble("Latitude");
                        Double currentLocationLongitude = getIntent().getExtras().getDouble("Longitude");
                        maxDataValue = maxData.getText().toString();
                        //to add ssid to SSID key in shared pref
                        SSIDlist.add(ssid.getText().toString());
                        SSIDlist.add("home");
                        editor.putStringSet(SSID, SSIDlist);
                        // editor.putString(ssid + SSID, ssid.getText().toString());
                        Log.i("CreateGeofence", Double.toString(currentLocationLatitude));
                        Log.i("CreateGeofence", Double.toString(currentLocationLongitude));

                        editor.putString(ssid.getText().toString() + LATITUDE, Double.toString(currentLocationLatitude));
                        editor.putString("home" + LATITUDE, Double.toString(28.584930));
                        // Log.i("CreateGeofence", ssid + LATITUDE);

                        editor.putString(ssid.getText().toString() + LONGITUDE, Double.toString(currentLocationLongitude));
                        editor.putString("home" + LONGITUDE, Double.toString(77.056711));

                        editor.putString(ssid.getText().toString()+ RADIUS,radius.getText().toString());
                        editor.putString("home"+ RADIUS,"100");

                        editor.putString(ssid.getText().toString() + PASSWORD, password.getText().toString());
                        editor.putString("home" + PASSWORD, "home");

                        editor.putString(ssid.getText().toString() + DATA, maxDataValue);
                        editor.putString("home" + DATA, "200");
                        editor.commit();

                        Log.i("CreateGeofence", "starting MainActivity");
                        Intent intent = new Intent(CreateGeofence.this, GeofenceActivity.class);
                        CreateGeofence.this.startActivity(intent);

                    }
                }



            }
        });
    }
}

