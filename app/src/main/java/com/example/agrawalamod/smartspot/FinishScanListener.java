package com.example.agrawalamod.smartspot;
import java.util.ArrayList;
/**
 * Created by agrawalamod on 10/24/15.
 */
public interface FinishScanListener {



    // Interface called when the scan method finishes. Network operations should not execute on UI thread

    public void onFinishScan(ArrayList<ClientScanResult> clients);

}
