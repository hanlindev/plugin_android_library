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
    private static boolean bluetoothConnected;
    private Context mContext;
    public  FeatureChecker(Context mContext){
        this.mContext = mContext;
        this.bluetoothConnected = false;
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
        Integer currentAPI = Integer.valueOf(android.os.Build.VERSION.SDK);
        if(currentAPI >=14) {
            BluetoothAdapter mAdapter;
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            int status= mAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
            System.out.println("status = "+ status);
            bluetoothConnected = status==2;
        }
        else {
            IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
            final BroadcastReceiver mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    String action = intent.getAction();
                    bluetoothConnected = BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) ? true : false;
                }
            };
            mContext.registerReceiver(mReceiver, filter1);
        }
        return bluetoothConnected;
    }
    /*
    public void unregister(){
        mContext.unregisterReceiver(mReceiver);
    }*/
    // return true is there is a valid bluetooth connectivity
    // return false if there is no

    private boolean packageManagerHasFeature(Feature feature) {
        return this.pm.hasSystemFeature(feature.toString());
    }
}

