package com.example.heartbeataudiodetector;

import java.io.IOException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
//import com.sana.android.plugin.hardware.BluetoothDevice;


public class RecordHeartbeat extends Activity {

    private static final String mFileName = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/audiorecordtest.3gp";
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String TAG = "Heartbeat";
    private static final long DEFAULT_CLIP_TIME = 1000;
    private long clipTime = DEFAULT_CLIP_TIME;
    private AmplitudeClipListener clipListener;

    private boolean continueRecording;

    /**
     * how much louder is required to hear a clap 10000, 18000, 25000 are good
     * values
     */
    private int amplitudeThreshold;

    /**
     * requires a little of noise by the user to trigger, background noise may
     * trigger it
     */
    public static final int AMPLITUDE_DIFF_LOW = 10000;
    public static final int AMPLITUDE_DIFF_MED = 18000;
    /**
     * requires a lot of noise by the user to trigger. background noise isn't
     * likely to be this loud
     */
    public static final int AMPLITUDE_DIFF_HIGH = 25000;

    private static final int DEFAULT_AMPLITUDE_DIFF = AMPLITUDE_DIFF_MED;
    private AudioManager mAudioManager;
    //private BluetoothDevice BD = new BluetoothDevice();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_heartbeat);

        final ToggleButton mRecordButton = (ToggleButton) findViewById(R.id.record_button);
        final ToggleButton mPlayButton = (ToggleButton) findViewById(R.id.play_button);

        // Set up record Button
        mRecordButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Set checked state
                mPlayButton.setEnabled(!isChecked);
                // Start/stop recording
                onRecordPressed(isChecked);
            }
        });

        // Set up play Button
        mPlayButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Set checked state
                mRecordButton.setEnabled(!isChecked);
                // Start/stop playback
                onPlayPressed(isChecked);
            }
        });


    }

    // Toggle recording
    private void onRecordPressed(boolean shouldStartRecording) {
        if(shouldStartRecording) {
            startRecording();
        }
        else{
            stopRecording();
        }
    }

    // Start recording with MediaRecorder
    //original startRecording Class
    private void startRecording() {
        boolean clapDetected = false;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare and start MediaRecorder");
        }
        mRecorder.start();
        int startAmplitude = mRecorder.getMaxAmplitude();
        Log.d(TAG, "starting amplitude: " + startAmplitude);

        continueRecording = true;
        int heartbeatCount=0;
        do
        {
            amplitudeThreshold = 10000;
            Log.d(TAG, "waiting while recording...");
            waitSome();
            int finishAmplitude = mRecorder.getMaxAmplitude();
            if (clipListener != null)
            {
                clipListener.heard(finishAmplitude);
            }

            int ampDifference = finishAmplitude - startAmplitude;
            if (ampDifference >= amplitudeThreshold)
            {
                Log.d(TAG, "heard a heartbeat!");
                heartbeatCount++;
            }
            Log.d(TAG, "finishing amplitude: " + finishAmplitude + " diff: "
                    + ampDifference);
        } while (continueRecording && heartbeatCount < 5);

        //Log.d(TAG, "stopped recording");
        System.out.println(heartbeatCount);
        //return clapDetected;
    }

    private void waitSome()
    {
        try
        {
            // wait a while
            Thread.sleep(clipTime);
        } catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted");
        }
    }

    // Stop recording. Release resources
    private void stopRecording() {

        //BluetoothDevice bluetoothMic = new BluetoothDevice();
        if (null != mRecorder) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    // Toggle playback
    private void onPlayPressed(boolean shouldStartPlaying) {
        if (shouldStartPlaying) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    // Playback audio using MediaPlayer
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare and start MediaPlayer");
        }
    }

    // Stop playback. Release resources
    private void stopPlaying() {
        if (null != mPlayer) {
            if (mPlayer.isPlaying())
                mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
    // Release recording and playback resources, if necessary
    @Override
    public void onPause() {
        super.onPause();

        if (null != mRecorder) {
            mRecorder.release();
            mRecorder = null;
        }

        if (null != mPlayer) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
