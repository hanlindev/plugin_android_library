package com.sana.android.plugin.hardware;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;

import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
/**
 * Created by hanlin on 9/14/14.
 */
public class BluetoothDevice extends AudioRecordDevice implements GeneralDevice {
    private ContentResolver contentResolver;
    private static final String TAG = "AudioRecordTest";
    private AudioManager mAudioManager;
    private Context mContext;
    //prepare is to store location of recorded audio?

    public BluetoothDevice(Context mContext){
        this.mContext = mContext;

    }
    public DataWithEvent prepare() {
        //CommManager cm = CommManager.getInstance();
        //File initialFile = new File(super.mFileName);
        //InputStream is = new FileInputStream(initialFile);
        //BinaryDataWithPollingEvent pollingData = new BinaryDataWithPollingEvent(Feature.BLUETOOTH, CommManager.getInstance().getMimeType(), cm.getUri(), this, int uri_size);
        return null;
    }

    public void startBluetoothMic(){
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mContext.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d(TAG, "Audio SCO state: " + state);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    // now the connection has be established to the bluetooth device
                    mContext.getApplicationContext().unregisterReceiver(this);
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
        Log.d(TAG, "starting bluetooth");
        mAudioManager.startBluetoothSco();
    }
}
