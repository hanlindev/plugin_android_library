package com.example.heartbeataudiodetector;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

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
    private static final int NUM_SECONDS_NEEDED = 15;
    private static final int PROGRESS_BOUNDARY = NUM_SECONDS_NEEDED / 360;

    private CaptureManager captureManager;
    //private AccelerometerEventListener listener;
    private boolean isRecording;
    private LinkedList<AccelerometerDataEvent.AccelerometerData> recordings;
    private ProgressWheel progressWheel;
    private int progress;
    private VerticalPager verticalPager;
    private ProgressBar spinner;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Object[] data = (Object[]) msg.obj;
            //AccelerometerDataEvent.AccelerometerData[] castData = Arrays.copyOf(data, data.length, AccelerometerDataEvent.AccelerometerData[].class);
            //recordings.addAll(Arrays.asList(castData));

            updateUi();
            long timeElapsed=System.nanoTime() - startTime;
            if (timeElapsed/1000000000 >= 15 ) {
                if (progress < 360) {
                    progressWheel.setProgress(360);
                }

                // add notification sound while done
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressWheel.setText("Done");
                //listener.stopListening();
                //captureManager.stop();
                verticalPager.scrollDown();
            }
        }

        private void updateUi() {
            TextView readingCount = (TextView) findViewById(R.id.readingCount);
            readingCount.setText("Readings: " + heartbeatCount);

            long timeElapsed=System.nanoTime() - startTime;
            progressWheel.setProgress(360*(int)timeElapsed/1000000000/15);
            /*
            if (timeElapsed/ PROGRESS_BOUNDARY > progress) {
                progress = recordings.size() / PROGRESS_BOUNDARY;
                progressWheel.incrementProgress();
            }
            */
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

        // Set up record Button
        heartbeatCount = 0;

    }

    // Start recording with MediaRecorder
    //original startRecording Class
    public void startRecording(View view) {
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
                    duration = System.nanoTime() - startTime;
                    if(duration/1000000000 > 15)
                        continueRecording = false;
                    amplitudeThreshold = 5000;
                    Log.d(TAG, "waiting while recording...");
                    waitSome();
                    int finishAmplitude = mRecorder.getMaxAmplitude();
                    //updateUI();
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

    private void updateUI(){
        TextView readingCount = (TextView) findViewById(R.id.readingCount);
        readingCount.setText("Readings: " + heartbeatCount);
        long timeElapsed = System.nanoTime() - startTime;
        progressWheel.setProgress(360*(int)timeElapsed/1000000000/15);
                /*
                if (recordings.size() / PROGRESS_BOUNDARY > progress) {
                    progress = recordings.size() / PROGRESS_BOUNDARY;
                    progressWheel.incrementProgress();
                }*/
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
