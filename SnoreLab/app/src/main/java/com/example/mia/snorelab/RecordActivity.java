package com.example.mia.snorelab;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.Feature;



public class RecordActivity extends Activity {
    private static final String TAG = "Snore Recording";
    private static final String RECORDING = "Recording... ";
    private static final String RECORDING_FINISH = "Recording finished. ";
    private static final String SEND_ERROR_MESSAGE =
            "Please finish recording heartbeat before sending data";
    private static long startTime;
    private boolean isRecording;
    private boolean stopped;
    private CaptureManager captureManager;
    private VerticalPager verticalPager;
    private ProgressBar spinner;

    // Handler is used to update UI on the UI thread
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(stopped){
                verticalPager.scrollDown();
                try {
                    Uri notification = RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(
                            getApplicationContext(),
                            notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            updateUI();
        }

        private void updateUI(){
            TextView readingCount = (TextView) findViewById(R.id.readingCount);
            if(isRecording) {
                readingCount.setText(RECORDING );
            }else{
                readingCount.setText(RECORDING_FINISH );
            }

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        verticalPager = (VerticalPager) findViewById(R.id.verticalPager);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        // receive launch intent from sana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);

        isRecording = false;
        stopped = false;
    }

    public void startRecording(View view) {
        if(!isRecording) {
            try {
                captureManager = new CaptureManager(
                        Feature.MICROPHONE,
                        MimeType.AUDIO,
                        getContentResolver());
                captureManager.prepare();
            } catch (Exception o) {

            }
            captureManager.begin();
            startTime = System.nanoTime();
            isRecording = true;
            stopped = false;
            handler.sendEmptyMessage(0);

        }else{
            if(captureManager != null) {
                captureManager.stop();
            }
            isRecording = false;
            stopped = true;
            handler.sendEmptyMessage(0);
        }
    }

    // Release recording and playback resources, if necessary
    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    public void sendDataToSana(View view) {
        if (stopped) {
            CommManager cm = CommManager.getInstance();
            cm.sendData(this);
        } else {
            Toast errorToast = Toast.makeText(
                    getApplicationContext(),
                    SEND_ERROR_MESSAGE,
                    Toast.LENGTH_SHORT
            );
            errorToast.show();
        }
    }

}

