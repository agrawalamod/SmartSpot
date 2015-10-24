package com.example.agrawalamod.smartspot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity4 extends AppCompatActivity {

    TextView textView1;
    WifiApManager wifiApManager;
    Button startHotspot;
    Button stopHotspot;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        startHotspot = (Button) findViewById(R.id.button3);
        stopHotspot = (Button) findViewById(R.id.button4);
        startHotspot.setOnClickListener(myhandler1);
        stopHotspot.setOnClickListener(myhandler2);

        textView1 = (TextView) findViewById(R.id.resultView);
        wifiApManager = new WifiApManager(this);
        //wifiApManager.setHotspotSettings("MC Demo", "testtest");
        //wifiApManager.setWifiApEnabled(true);
        //wifiApManager.viewHotspotSettings();
        scan();

    }

    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                textView1.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
                textView1.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    textView1.append("####################\n");
                    textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                    textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
                }
            }
        });
    }
    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            //wifiApManager.setHotspotSettings("MC Demo", "testtest");
            //wifiApManager.setWifiApEnabled(true);
            //wifiApManager.viewHotspotSettings();
        }
    };
    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            wifiApManager.setWifiApEnabled(false);

        }
    };





}
