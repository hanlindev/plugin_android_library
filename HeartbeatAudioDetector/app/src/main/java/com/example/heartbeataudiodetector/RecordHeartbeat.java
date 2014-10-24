package com.example.heartbeataudiodetector;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
import java.text.DecimalFormat;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.AccelerometerDataEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;
//import com.sana.android.plugin.hardware.BluetoothDevice;


public class RecordHeartbeat extends Activity {

    private static final String mFileName = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/audiorecordtest.3gp";
    private static MediaRecorder mRecorder;
    private static final String TAG = "Heartbeat";
    private static final long DEFAULT_CLIP_TIME = 300;
    private long clipTime = DEFAULT_CLIP_TIME;
    private static int heartbeatCount;
    private static Thread calculateThread;
    private static boolean continueRecording;
    private static long startTime;
    private static long duration;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_heartbeat);

        final ToggleButton mRecordButton = (ToggleButton) findViewById(R.id.record_button);

        // Set up record Button
        mRecordButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // Start/stop recording
                onRecordPressed(isChecked);
            }
        });
        heartbeatCount = 0;

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
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Couldn't prepare and start MediaRecorder");
        }
        mRecorder.start();
        startTime = System.nanoTime();
        // ... do recording ...

        continueRecording = true;
        calculateThread = new Thread(new Runnable() {
            public void run() {
                int startAmplitude = mRecorder.getMaxAmplitude();
                Log.d(TAG, "starting amplitude: " + startAmplitude);
                heartbeatCount = 0;
                do {
                    amplitudeThreshold = 5000;
                    Log.d(TAG, "waiting while recording...");
                    waitSome();
                    int finishAmplitude = mRecorder.getMaxAmplitude();
                    int ampDifference = finishAmplitude - startAmplitude;
                    if (ampDifference >= amplitudeThreshold) {
                        Log.d(TAG, "heard a heartbeat!");
                        heartbeatCount++;
                    }
                    System.out.println(heartbeatCount);
                    Log.d(TAG, "finishing amplitude: " + finishAmplitude + " diff: "
                            + ampDifference);
                } while (continueRecording);
            }
        });
        calculateThread.start();
        Log.d(TAG, "stopped recording");
        System.out.println(heartbeatCount);
    }

    private void waitSome()
    {
        try{
            // wait a while
            Thread.sleep(clipTime);
        } catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted");
        }
    }

    // Stop recording. Release resources
    private void stopRecording() {
        continueRecording =false;
        calculateThread.interrupt();
            if (null != mRecorder) {
                mRecorder.stop();
                duration = System.nanoTime() - startTime;
                mRecorder.release();
                mRecorder = null;
                DecimalFormat df = new DecimalFormat("#.##");
                new AlertDialog.Builder(this)
                        .setTitle("Recording Finished")
                        .setMessage("Your heartbeat is "+df.format(60*heartbeatCount/(duration/1000000000.0))+" beats/min.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
    }
}
