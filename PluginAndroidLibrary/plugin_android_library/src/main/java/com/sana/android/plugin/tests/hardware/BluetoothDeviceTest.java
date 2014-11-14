package com.sana.android.plugin.tests.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.test.*;
import android.util.Log;
import java.io.*;

import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.hardware.BluetoothAudioDevice;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;

import junit.framework.Assert;

/**
 * Created by zhaoyue on 3/10/14.
 */
    public class BluetoothDeviceTest extends InstrumentationTestCase {

        private BluetoothAudioDevice BD;
        private Context mContext;
        private static final String TAG = "BluetoothDeviceTest";

        public void testPrepare(){
            BD = new BluetoothAudioDevice();
            BD.prepare();
            Assert.assertNotNull(BD.getmRecorder());
        }

        public void testStop(){
            BD = new BluetoothAudioDevice();
            BD.begin();
            BD.stop();
            Assert.assertNull(BD.getmRecorder());
        }
/*
        public void testBluetoothMicChannel() throws Exception {
            BD = new BluetoothAudioDevice();
            BD.prepare();
            BD.begin();
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


        public void testSetCaptureSetting(CaptureSetting cm){
            BD = new BluetoothAudioDevice();
            BD.setCaptureSetting(cm);
            Assert.assertEquals(BD.getAudioEncoder(), (int)cm.getAudioEncoder());
            Assert.assertEquals(BD.getAudioSource(),(int)cm.getAudioSource());
            Assert.assertEquals(BD.getOutputFormat(),(int)cm.getOutputFormat());
            Assert.assertEquals(BD.getResolver(), cm.getContentResolver());
            Assert.assertEquals(BD.getContext(),cm.getApplicationContext());
            Assert.assertEquals(BD.getFileName(),cm.getOutputFileName());
        }

        public void testMoveData(){
            BD = new BluetoothAudioDevice();
            BD.begin();
            BD.stop(); // move data is inside stop
            File f = new File(CommManager.getInstance().getUri().toString());
            Assert.assertEquals(f.exists(), true);
            Assert.assertEquals(f.isDirectory(), false);
        }


        public void testReset(){
            BD = new BluetoothAudioDevice();
            BD.begin();
            BD.reset();
            Assert.assertNull(BD.getFileName());
        }
        */
    }
