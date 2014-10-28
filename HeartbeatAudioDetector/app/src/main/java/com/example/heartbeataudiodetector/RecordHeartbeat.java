package com.example.heartbeataudiodetector;

import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.AudioRecordDevice;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;

public class RecordHeartbeat extends Activity {
    private static final String TAG = "Heartbeat";
    private static final long DEFAULT_CLIP_TIME = 300;
    private long clipTime = DEFAULT_CLIP_TIME;
    private static int heartbeatCount;
    private static Thread calculateThread;
    private static boolean continueRecording;
    private static long startTime;
    private static long duration;
    private static final int NUM_SECONDS_NEEDED = 15;

    private CaptureManager captureManager;
    private ProgressWheel progressWheel;
    private VerticalPager verticalPager;
    private ProgressBar spinner;
    private int amplitudeThreshold;

    // Handler is used to update UI on the UI thread
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            long timeElapsed = System.nanoTime() - startTime;
            if((int)(timeElapsed/1000000000)>NUM_SECONDS_NEEDED){
                verticalPager.scrollDown();
                // create a sound notification when time is up
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            updateUI();
        }

        private void updateUI(){
            TextView readingCount = (TextView) findViewById(R.id.readingCount);
            readingCount.setText("Heartbeat Readings: " + heartbeatCount);
            long timeElapsed = System.nanoTime() - startTime;
            progressWheel.setProgress((int)(360*timeElapsed/1000000000)/NUM_SECONDS_NEEDED);
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_heartbeat);

        progressWheel = (ProgressWheel) findViewById(R.id.startButton);
        verticalPager = (VerticalPager) findViewById(R.id.verticalPager);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        // receive launch intent from sana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);
        // prepare data
        heartbeatCount = 0;
        amplitudeThreshold = 5000;

        captureManager = new CaptureManager(Feature.MICROPHONE, MimeType.AUDIO, getContentResolver());
    }

    // Start recording with MediaRecorder
    //original startRecording Class
    public void startRecording(View view) {
        if(!continueRecording) {
            ProgressWheel start = (ProgressWheel) findViewById(R.id.startButton);
            start.setText("Recording");
            captureManager.prepare();
            captureManager.begin();
            startTime = System.nanoTime();
            // ... do recording ...
            continueRecording = true;
            calculateThread = new Thread(new Runnable() {
                public void run() {
                    AudioRecordDevice mic = (AudioRecordDevice) captureManager.getDevice();
                    int startAmplitude = mic.getmRecorder().getMaxAmplitude();
                    Log.d(TAG, "starting amplitude: " + startAmplitude);
                    heartbeatCount = 0;
                    do {
                        duration = System.nanoTime() - startTime;
                        if (duration / 1000000000 > NUM_SECONDS_NEEDED) {
                            continueRecording = false;
                        }
                        Log.d(TAG, "waiting while recording...");
                        waitSome();
                        int finishAmplitude = mic.getmRecorder().getMaxAmplitude();
                        int ampDifference = finishAmplitude - startAmplitude;
                        if (ampDifference >= amplitudeThreshold) {
                            Log.d(TAG, "heard a heartbeat!");
                            heartbeatCount++;
                        }
                        handler.sendEmptyMessage(0);
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
    }

    private void waitSome()
    {
        try{  // wait some time
            Thread.sleep(clipTime);
        }
        catch (InterruptedException e)
        {
            Log.d(TAG, "interrupted");
        }
    }
    // Release recording and playback resources, if necessary
    @Override
    protected void onStop() {
        super.onStop();
        captureManager.stop();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    public void sendDataToSana(View view) {
        if (!continueRecording) {
            CommManager cm = CommManager.getInstance();
            cm.sendData(this, getDataString());
        } else {
            Toast errorToast = Toast.makeText(
                    getApplicationContext(),
                    "Please finish recording heartbeat before sending data",
                    Toast.LENGTH_SHORT
            );
            errorToast.show();
        }
    }

    private String getDataString() {
        return "Your heartbeat is "+Integer.toString(4*heartbeatCount)+" beats/min.";
    }
}
