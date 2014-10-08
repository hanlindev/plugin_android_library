package com.sana.android.plugin.tests.hardware;

import android.test.InstrumentationTestCase;

import com.sana.android.plugin.hardware.AudioRecordDevice;
import com.sana.android.plugin.hardware.BuiltinAudioDevice;

/**
 * Created by Mia on 7/10/14.
 */
public class BuiltinAudioDeviceTest extends InstrumentationTestCase {
    private BuiltinAudioDevice audioRecord = new BuiltinAudioDevice();
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
