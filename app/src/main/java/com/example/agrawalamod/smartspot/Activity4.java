package com.example.agrawalamod.smartspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class Activity4 extends AppCompatActivity {

    TextView textView1;
    WifiApManager wifiApManager;
    Button startHotspot;
    Button stopHotspot;
    Button clients;
    Button gpsConnect; //Button is currently redundant. I shifted the geofencing stuff into the mainactivity, so it handles both Google login and Geofencing
    Activity4 activity;
    EditText ssid;
    EditText passkey;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);
        activity = this;

        startHotspot = (Button) findViewById(R.id.button3);
        stopHotspot = (Button) findViewById(R.id.button4);
        clients = (Button) findViewById(R.id.button6);
        gpsConnect = (Button) findViewById(R.id.button7);
        ssid = (EditText) findViewById(R.id.ssid);
        passkey = (EditText) findViewById(R.id.passkey);

        startHotspot.setOnClickListener(myhandler1);
        stopHotspot.setOnClickListener(myhandler2);
        clients.setOnClickListener(myhandler3);
        gpsConnect.setOnClickListener(myhandler4);

        textView1 = (TextView) findViewById(R.id.resultView);
        wifiApManager = new WifiApManager(this);
        if(wifiApManager.isWifiApEnabled())
        {
            textView1.setText("Hotspot is running");
        }
        else
        {
            textView1.setText("Hotspot is not running");
        }

    }


    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {

            System.out.println("Button pressed");
            String networkName = ssid.getText().toString();
            String password = passkey.getText().toString();
            if(password.length() >=8) {
                if(!wifiApManager.isWifiApEnabled())
                {

                    wifiApManager.setHotspotSettings(networkName, password);
                    if (wifiApManager.setWifiApEnabled(true)) {
                        System.out.println("WiFi Hotspot Started");
                        Toast.makeText(getApplicationContext(), "Portable Hotspot started", Toast.LENGTH_LONG).show();
                        textView1.setText("Hotspot is running");
                    }
                    wifiApManager.viewHotspotSettings();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "WPA Passkey must be atleast 8 characters", Toast.LENGTH_LONG).show();
            }

        }
    };
    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {

            if(wifiApManager.setWifiApEnabled(false))
            {
                System.out.println("WiFi Hotspot Stopped");
                Toast.makeText(getApplicationContext(), "Portable Hotspot stopped", Toast.LENGTH_LONG).show();
                textView1.setText("Hotspot is not running");

            }

        }
    };
    View.OnClickListener myhandler3 = new View.OnClickListener() {
        public void onClick(View v) {

            Intent intent = new Intent(activity, ConnectedClients.class);
            startActivity(intent);

        }
    };


    View.OnClickListener myhandler4 = new View.OnClickListener() {
        public void onClick(View v) {


        }
    };


}
