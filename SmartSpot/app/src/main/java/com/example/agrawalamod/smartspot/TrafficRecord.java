package com.example.agrawalamod.smartspot;

import android.net.TrafficStats;

public class TrafficRecord {
    long tx=0;
    long rx=0;
    String tag=null;

    TrafficRecord() {
        tx=TrafficStats.getTotalTxBytes();
        rx=TrafficStats.getTotalRxBytes();
    }

    TrafficRecord(int uid, String tag) {
        tx=TrafficStats.getUidTxBytes(uid);
        rx=TrafficStats.getUidRxBytes(uid);
        this.tag=tag;
    }
}