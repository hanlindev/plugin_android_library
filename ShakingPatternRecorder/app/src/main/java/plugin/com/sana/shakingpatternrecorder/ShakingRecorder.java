package plugin.com.sana.shakingpatternrecorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.AccelerometerDataEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;
import com.sana.android.plugin.data.event.AccelerometerDataEvent.AccelerometerData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


public class ShakingRecorder extends ActionBarActivity {
    private class AccelerometerEventListener extends TimedListener {
        public AccelerometerEventListener(Object sender) {
            super(sender, RECORDING_INTERVAL, RECORDING_INTERVAL_UNIT);
        }

        @Override
        public synchronized void processData(Object sender, Object[] data) {
            if (data.length > 0) {
                if (recordings.size() < NUM_RECORDINGS_NEEDED) {
                    Message msg = handler.obtainMessage();
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private static final int NUM_RECORDINGS_NEEDED = 1000;
    private static final int PROGRESS_BOUNDARY = NUM_RECORDINGS_NEEDED / 360 + 1;
    private static final long RECORDING_INTERVAL = 1;
    private static final TimeUnit RECORDING_INTERVAL_UNIT = TimeUnit.MILLISECONDS;

    private CaptureManager captureManager;
    private AccelerometerEventListener listener;
    private boolean isRecording;
    private LinkedList<AccelerometerData> recordings;
    private ProgressWheel progressWheel;
    private int progress;
    private VerticalPager verticalPager;
    private ProgressBar spinner;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object[] data = (Object[]) msg.obj;
            AccelerometerData[] castData = Arrays.copyOf(data, data.length, AccelerometerData[].class);
            recordings.addAll(Arrays.asList(castData));

            updateUi();

            if (recordings.size() >= NUM_RECORDINGS_NEEDED) {
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
                listener.stopListening();
                captureManager.stop();
                verticalPager.scrollDown();
            }
        }

        private void updateUi() {
            TextView readingCount = (TextView) findViewById(R.id.readingCount);
            readingCount.setText("Readings: " + recordings.size());

            if (recordings.size() / PROGRESS_BOUNDARY > progress) {
                progress = recordings.size() / PROGRESS_BOUNDARY;
                progressWheel.incrementProgress();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaking_recorder);
        progressWheel = (ProgressWheel) findViewById(R.id.startButton);
        verticalPager = (VerticalPager) findViewById(R.id.verticalPager);

        // receive launch intent from sana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        // Prepare data
        recordings = new LinkedList<AccelerometerData>();
        progress = 0;

        // Prepare CaptureManager
        isRecording = false;
        CaptureSetting newSetting = CaptureSetting.defaultSetting(Feature.ACCELEROMETER, MimeType.TEXT_PLAIN)
                .setSensorManager((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        captureManager = new CaptureManager(Feature.ACCELEROMETER, MimeType.TEXT_PLAIN, getContentResolver(), newSetting);
        listener = new AccelerometerEventListener(captureManager);
        captureManager.addListener(listener);
        captureManager.prepare();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shaking_recorder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        captureManager.stop();
        listener.stopListening();
    }

    protected void onPause() {
        super.onPause();
        captureManager.removeListener(listener);
    }

    protected void onResume() {
        super.onResume();
        captureManager.addListener(listener);
    }

    /**
     * Called when start recording button is clicked
     * @param view
     */
    public void recordFromAccelerometer(View view) {
        if (!isRecording) {
            ProgressWheel start = (ProgressWheel)findViewById(R.id.startButton);
            start.setText("Recording");
            start.invalidate();
            isRecording = true;
            listener.startListening();
            captureManager.begin();
        }
    }

    public void sendDataToSana(View view) {
        if (recordings.size() >= NUM_RECORDINGS_NEEDED) {
            spinner.setVisibility(View.VISIBLE);
            CommManager cm = CommManager.getInstance();
            cm.sendData(this, getDataString());
        } else {
            Toast errorToast = Toast.makeText(
                    getApplicationContext(),
                    "Please finish recording before sending data",
                    Toast.LENGTH_SHORT
            );
            errorToast.show();
        }
    }

    private String getDataString() {
        return Arrays.toString(recordings.toArray());
    }
}
