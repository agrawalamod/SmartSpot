package com.example.snipeswipe.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class TrafficMonitorActivity extends Activity {
    TextView latest_rx=null;
    TextView latest_tx=null;
    TextView previous_rx=null;
    TextView previous_tx=null;
    TextView delta_rx=null;
    TextView delta_tx=null;
    TextView total=null;

    Button button=null;

    TrafficSnapshot latest=null;
    TrafficSnapshot previous=null;
    double tot=0;
    int count = 100;

    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latest_rx=(TextView)findViewById(R.id.latest_rx);
        latest_tx=(TextView)findViewById(R.id.latest_tx);
        previous_rx=(TextView)findViewById(R.id.previous_rx);
        previous_tx=(TextView)findViewById(R.id.previous_tx);
        delta_rx=(TextView)findViewById(R.id.delta_rx);
        delta_tx=(TextView)findViewById(R.id.delta_tx);
        total=(TextView)findViewById(R.id.total);

        button=(Button) findViewById(R.id.button);

        takeSnapshot();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//                button.performClick();
//
//                Log.d("TrafficMonitor", "Automated Running");
//
//            }
//        }, 500);

        buttonFunction();

         //Declare as inatance variable

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final Toast toast = Toast.makeText(
                                getApplicationContext(), tot + "",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        takeSnapshot();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);

                    }
                });
            }
        }, 0, 1000);
    }

    public void buttonFunction(){
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                takeSnapshot();
                //startService(new Intent(getBaseContext(), TrafficService.class));
                //stopService(new Intent(getBaseContext(), TrafficService.class));
            }

        });
    }



    public void takeSnapshot() {
        previous=latest;
        latest=new TrafficSnapshot(this);

        latest_rx.setText(String.valueOf(latest.device.rx));
        latest_tx.setText(String.valueOf(latest.device.tx));

        if (previous!=null) {
            previous_rx.setText(String.valueOf(previous.device.rx));
            previous_tx.setText(String.valueOf(previous.device.tx));

            delta_rx.setText(String.valueOf(latest.device.rx-previous.device.rx));
            delta_tx.setText(String.valueOf(latest.device.tx-previous.device.tx));

            tot=tot+latest.device.rx-previous.device.rx+latest.device.tx - previous.device.tx;
            total.setText(String.valueOf(tot));
        }

        ArrayList<String> log=new ArrayList<String>();
        HashSet<Integer> intersection=new HashSet<Integer>(latest.apps.keySet());

        if (previous!=null) {
            intersection.retainAll(previous.apps.keySet());
        }

        for (Integer uid : intersection) {
            TrafficRecord latest_rec=latest.apps.get(uid);
            TrafficRecord previous_rec=
                    (previous==null ? null : previous.apps.get(uid));

            emitLog(latest_rec.tag, latest_rec, previous_rec, log);
        }

        Collections.sort(log);

        for (String row : log) {
            Log.d("TrafficMonitor", row);
        }
    }

    private void emitLog(CharSequence name, TrafficRecord latest_rec,
                         TrafficRecord previous_rec,
                         ArrayList<String> rows) {
        if (latest_rec.rx>-1 || latest_rec.tx>-1) {
            StringBuilder buf=new StringBuilder(name);

            buf.append("=");
            buf.append(String.valueOf(latest_rec.rx));
            buf.append(" received");

            if (previous_rec!=null) {
                buf.append(" (delta=");
                buf.append(String.valueOf(latest_rec.rx-previous_rec.rx));
                buf.append(")");
            }

            buf.append(", ");
            buf.append(String.valueOf(latest_rec.tx));
            buf.append(" sent");

            if (previous_rec!=null) {
                buf.append(" (delta=");
                buf.append(String.valueOf(latest_rec.tx-previous_rec.tx));
                buf.append(")");
            }

            rows.add(buf.toString());
        }
    }
}