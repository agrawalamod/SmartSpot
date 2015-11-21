package com.example.agrawalamod.smartspot;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ConnectedClients extends AppCompatActivity {


    TextView textView;
    WifiApManager wifiApManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_clients);
        textView = (TextView) findViewById(R.id.result);
        wifiApManager = new WifiApManager(this);
        //wifiApManager = (WifiApManager) getIntent().getSerializableExtra("wifimanager");;
        scan();

    }
    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                textView.setText("AP State: " + wifiApManager.getWifiApState() + "\n\n");
                textView.append("Clients: \n\n");
                for (ClientScanResult clientScanResult : clients) {
                    textView.append("----------------------------\n");
                    textView.append("IP Address: " + clientScanResult.getIpAddr() + "\n");
                    textView.append("Device: " + clientScanResult.getDevice() + "\n");
                    textView.append("Mac Address: " + clientScanResult.getHWAddr() + "\n");
                    textView.append("Is Reachable: " + clientScanResult.isReachable() + "\n");
                }
            }
        });
    }

}
