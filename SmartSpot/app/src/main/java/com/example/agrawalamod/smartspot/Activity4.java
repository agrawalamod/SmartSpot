package com.example.agrawalamod.smartspot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class Activity4 extends AppCompatActivity {

    TextView textView1;
    WifiApManager wifiApManager;
    Button startHotspot;
    Button stopHotspot;
    Button clients;
    Button configure;


    Activity4 activity;
    TextView ssid;
    TextView passkey;

    String latitude;
    String longitude;
    String password;
    String SSID;

    TrafficSnapshot latest=null;
    TrafficSnapshot previous=null;
    TrafficSnapshot InitialData=null;
    double tot=0;
    Double sessionData =0.0;
    int count = 100;
    int i=1;
    long dataLimit = 200;

    TextView dataUsage;
    Boolean limitExceeded=false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);
        Intent i = getIntent();
        latitude = i.getStringExtra("Latitude");
        longitude = i.getStringExtra("Longitude");
        password = i.getStringExtra("Password");
        SSID = i.getStringExtra("SSID");
        dataLimit = Integer.parseInt(i.getStringExtra("MaxData"));

        activity = this;

        startHotspot = (Button) findViewById(R.id.button3);
        stopHotspot = (Button) findViewById(R.id.button4);
        clients = (Button) findViewById(R.id.button6);
        configure = (Button) findViewById(R.id.configure);
        ssid = (TextView) findViewById(R.id.textView16);
        passkey = (TextView) findViewById(R.id.textView17);

        dataUsage = (TextView) findViewById(R.id.dataUsage);

        startHotspot.setOnClickListener(myhandler1);
        stopHotspot.setOnClickListener(myhandler2);
        clients.setOnClickListener(myhandler3);
        configure.setOnClickListener(myhandler4);


        Log.i("Activity 4","Passkey: "+ password);
        textView1 = (TextView) findViewById(R.id.resultView);
        wifiApManager = new WifiApManager(this);
        wifiApManager.setHotspotSettings(SSID,password);
        //String networkName = "MC Demo";
        //String password = "testtest";

        if(wifiApManager.isWifiApEnabled())
        {
            textView1.setText("Hotspot is running");
        }
        else
        {
            textView1.setText("Hotspot is not running");
        }
        System.out.println(wifiApManager.getSSID());
        System.out.println(wifiApManager.getPasskey());
        ssid.setText("SSID: " + wifiApManager.getSSID());
        passkey.setText("Passkey: " + wifiApManager.getPasskey());





    }
    public void Stats()
    {
        //takeSnapshot();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        final Toast toast = Toast.makeText(
//                                getApplicationContext(), tot/(1024) + "",
//                                Toast.LENGTH_SHORT);
                        //toast.show();
                        if(takeSnapshot()==false)
                        {
                            timer.cancel();
                            timer.purge();

                        }
                        //i++;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                //toast.cancel();
                            }
                        }, 1000);

                    }
                });
            }
        }, 0, 1000);


    }


    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {

            System.out.println("Button pressed");
            //String networkName = ssid.getText().toString();
            //String password = passkey.getText().toString();

            //if(password.length() >=8) {
                if(!wifiApManager.isWifiApEnabled())
                {

                    //wifiApManager.setHotspotSettings(networkName, password);
                    if (wifiApManager.setWifiApEnabled(true)) {
                        System.out.println("WiFi Hotspot Started");
                        InitialData = new TrafficSnapshot(activity);
                        Toast.makeText(getApplicationContext(), "Portable Hotspot started", Toast.LENGTH_LONG).show();
                        textView1.setText("Hotspot is running");
                        limitExceeded = false;
                        sessionData =0.0;
                        Stats();

                    }
                    wifiApManager.viewHotspotSettings();
                }
           // }
            //else
            //{
                Toast.makeText(getApplicationContext(), "WPA Passkey must be atleast 8 characters", Toast.LENGTH_LONG).show();
            //}

        }
    };
    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {

            if(wifiApManager.setWifiApEnabled(false))
            {
                System.out.println("WiFi Hotspot Stopped");
                Toast.makeText(getApplicationContext(), "Portable Hotspot stopped", Toast.LENGTH_LONG).show();
                textView1.setText("Hotspot is not running");
                limitExceeded=false;

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

            Intent intent = new Intent(activity, WiFiConfigurationActivity.class);
            startActivity(intent);

        }
    };


    public Boolean takeSnapshot() {
        latest=new TrafficSnapshot(this);

        //latest_rx.setText(String.valueOf(latest.device.rx));
        //latest_tx.setText(String.valueOf(latest.device.tx));


            //previous_rx.setText(String.valueOf(previous.device.rx));
            // previous_tx.setText(String.valueOf(previous.device.tx));

            // delta_rx.setText(String.valueOf(latest.device.rx-previous.device.rx));
            //delta_tx.setText(String.valueOf(latest.device.tx-previous.device.tx));

            //tot=(tot+latest.device.rx-previous.device.rx+latest.device.tx - previous.device.tx);
            //TOTAL VALUE
        long val = ((latest.device.rx + latest.device.tx)-(InitialData.device.rx + InitialData.device.tx))/(1024*1024);
        dataUsage.setText("Data Usage: " + String.valueOf(((latest.device.rx + latest.device.tx) - (InitialData.device.rx + InitialData.device.tx)) / (1024 * 1024)) + " MB");
        Log.d("Total: ", String.valueOf((latest.device.rx + latest.device.tx)-(InitialData.device.rx + InitialData.device.tx)));
        if(val > dataLimit)
        {
            if(wifiApManager.setWifiApEnabled(false))
            {
                System.out.println("WiFi Hotspot Stopped");
                Toast.makeText(getApplicationContext(), "Portable Hotspot stopped", Toast.LENGTH_LONG).show();
                textView1.setText("Hotspot is not running");
                if(limitExceeded == false)
                {
                    Notify("SmartSpot", "Data Limit Exceeded");
                    limitExceeded=true;

                }
            }
            return false;
        }
        else
        {
            return true;
        }


    }
    private void Notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Notification notification = new Notification(R.drawable.ic_cast_dark,"Data Limit Exceeded", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this,Activity4.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

        //noinspection deprecation
        notification.setLatestEventInfo(Activity4.this, notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }





}
