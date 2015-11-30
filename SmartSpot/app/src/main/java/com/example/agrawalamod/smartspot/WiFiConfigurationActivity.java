package com.example.agrawalamod.smartspot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class WiFiConfigurationActivity extends AppCompatActivity {

    private static final String SSID = "SSID";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String PASSWORD = "PASSWORD";
    private static final String RADIUS = "RADIUS";
    EditText ssid;
    EditText passkey;
    Button saveSetting;
    WifiApManager wifiApManager;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    Set<String> SSIDlist = new HashSet<String>();
    TextView current;
    String currentSSID;
    String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_configuration);

        Intent i = getIntent();
        currentSSID = i.getStringExtra("SSID");
        currentPassword = i.getStringExtra("Password");


        sharedPref = this.getSharedPreferences("geofence", Context.MODE_APPEND);
        editor = sharedPref.edit();
        SSIDlist = sharedPref.getStringSet(SSID, SSIDlist);

        this.wifiApManager = new WifiApManager(this);
        ssid = (EditText) findViewById(R.id.ssid);
        passkey = (EditText) findViewById(R.id.passkey);
        saveSetting = (Button) findViewById(R.id.button9);
        saveSetting.setOnClickListener(myhandler1);
        current = (TextView) findViewById(R.id.textView19);
        current.setText("You are editing configurations for: " + currentSSID);


    }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {

            System.out.println("Save Button pressed");
            String networkName = ssid.getText().toString();
            String password = passkey.getText().toString();
            if(password.length() >=8) {

                if(SSIDlist.contains(currentSSID))
                {
                    editor.putString(ssid.getText().toString() + PASSWORD, currentPassword);
                    editor.commit();

                }
                else
                {
                    SSIDlist.remove(currentSSID);
                    SSIDlist.add(networkName);
                    editor.putStringSet(SSID, SSIDlist);

                    String lat = sharedPref.getString(currentSSID+LATITUDE, new String());
                    String lon = sharedPref.getString(currentSSID+LONGITUDE, new String());
                    String rad = sharedPref.getString(currentSSID+RADIUS, new String());


                    editor.remove(currentSSID + LATITUDE);
                    editor.remove(currentSSID+LONGITUDE);
                    editor.remove(currentSSID + PASSWORD);
                    editor.remove(currentSSID + RADIUS);

                    editor.putString(networkName + LONGITUDE, lon);
                    editor.putString(networkName + LATITUDE, lat);
                    editor.putString(networkName + RADIUS, rad);
                    editor.putString(networkName+PASSWORD, password);
                    editor.commit();



                }


                wifiApManager.setHotspotSettings(networkName, password);
                Toast.makeText(getApplicationContext(), "Configuration for Location saved!", Toast.LENGTH_LONG).show();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "WPA Passkey must be at least 8 characters", Toast.LENGTH_LONG).show();
            }

        }
    };

}
