package com.sana.android.plugin.hardware;

import android.bluetooth.*;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

/**
 * Created by Chen Xi on 9/2/2014.
 */
public class FeatureChecker {
    private PackageManager pm;
    private static boolean bluetoothConnected = false;
    private Context mContext;
    public  FeatureChecker(Context mContext){
        this.mContext = mContext;
    }

    public FeatureChecker(PackageManager pm) {
        this.pm = pm;
    }
    public boolean isFeatureAvailable(Feature feature) {
        return this.packageManagerHasFeature(feature);
    }


    // check current Connectivity Status
    public boolean isConnected(Feature feature){
        switch(feature){
            case BLUETOOTH:
                return checkBluetoothConnectivity();
        }

        return false;
    }

    // return true if there is a stable bluetooth connectivity
    private boolean checkBluetoothConnectivity(){
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                bluetoothConnected= BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)?true:false;
                context.unregisterReceiver(this);
            }
        };
        mContext.registerReceiver(mReceiver, filter1);
        return bluetoothConnected;
    }
    // return true is there is a valid bluetooth connectivity
    // return false if there is no

    private boolean packageManagerHasFeature(Feature feature) {
        return this.pm.hasSystemFeature(feature.toString());
    }
}

