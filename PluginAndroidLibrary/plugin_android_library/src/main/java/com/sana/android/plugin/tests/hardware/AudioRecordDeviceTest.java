package com.sana.android.plugin.tests.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.AudioRecordDevice;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;

/**
 * Created by Mia on 7/10/14.
 */
public class AudioRecordDeviceTest extends InstrumentationTestCase  {
    private AudioRecordDevice audioRecord = new AudioRecordDevice();
    private static final String TAG = "AudioRecordDeviceTest";

    public void testPauseRecorder(){
        audioRecord.reset();
        audioRecord.prepare();
        audioRecord.begin();
        audioRecord.pauseRecorder();
        assertEquals(audioRecord.getmRecorder(),null);
    }

    public void testPausePlayer(){
        audioRecord.reset();
        audioRecord.prepare();
        audioRecord.begin();
        audioRecord.pausePlayer();
        assertEquals(audioRecord.getmPlayer(),null);
    }

    public void testBegin(){
        audioRecord.reset();
        audioRecord.prepare();
        audioRecord.begin();
        assertNotSame(audioRecord.getmRecorder(),null);
    }

}
