package com.sana.android.plugin.tests.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.test.*;
import android.util.Log;

import com.sana.android.plugin.hardware.BluetoothDevice;

/**
 * Created by zhaoyue on 3/10/14.
 */
    public class BluetoothDeviceTest extends InstrumentationTestCase {

        private BluetoothDevice BD = new BluetoothDevice();
        private Context mContext;
        private static final String TAG = "BluetoothDeviceTest";

        public void testBluetoothMicChannel() throws Exception {
            BD.startBluetoothMic();
            mContext.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int currentState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                    Log.d(TAG, "Audio SCO state: " + currentState);
                    assertEquals(AudioManager.SCO_AUDIO_STATE_CONNECTED, currentState);
                }
            }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
        }

        public void testPauseRecorder(){
            assertEquals(BD.getmRecorder(),null);
        }

        public void testPausePlayer(){
            assertEquals(BD.getmPlayer(),null);
        }

        public void testBegin(){

        }

        public void testStop(){

        }

        public void testReset(){

        }

        public void testStartPlaying(){

        }

        public void testStopPlaying(){

        }
    }
