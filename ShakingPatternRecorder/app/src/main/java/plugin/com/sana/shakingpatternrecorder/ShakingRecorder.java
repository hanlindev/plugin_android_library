package plugin.com.sana.shakingpatternrecorder;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.AccelerometerDataEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;

import java.util.concurrent.TimeUnit;


public class ShakingRecorder extends ActionBarActivity {
    private class AccelerometerEventListener extends TimedListener {
        public AccelerometerEventListener(Object sender) {
            super(sender, 1, TimeUnit.SECONDS);
        }

        @Override
        public synchronized void processData(Object sender, Object[] data) {
            if (data.length > 0) {
                Log.d(AccelerometerEventListener.class.getName(), "Received data from event hahaha");
                Message msg = handler.obtainMessage();
                msg.obj = data[data.length - 1];
                handler.sendMessage(msg);
            }
        }
    }

    private CaptureManager captureManager;
    private AccelerometerEventListener listener;
    private boolean isRecording;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView textX = (TextView)findViewById(R.id.textViewX);
            TextView textY = (TextView)findViewById(R.id.textViewY);
            TextView textZ = (TextView)findViewById(R.id.textViewZ);

            AccelerometerDataEvent.AccelerometerData data = (AccelerometerDataEvent.AccelerometerData) msg.obj;
            double x = data.getX();
            double y = data.getY();
            double z = data.getZ();
            textX.setText(x + ", ");
            textY.setText(y+", ");
            textZ.setText(z+"");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaking_recorder);

        // receive launch intent from sana
        Intent intent = getIntent();
        CommManager cm = CommManager.getInstance();
        cm.respondToIntent(intent);

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
}
