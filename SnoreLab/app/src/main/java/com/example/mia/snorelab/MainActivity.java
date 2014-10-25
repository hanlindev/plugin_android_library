package com.example.mia.snorelab;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.data.listener.DataChunkListener;
import com.sana.android.plugin.hardware.Feature;
import android.util.Log;
import android.os.Handler;
import android.os.Message;


public class MainActivity extends Activity {

    public  CSurfaceView   	surfaceView;
    public  static short[]  buffer;
    public  static int      bufferSize;     // in bytes
    private static final int  FS = 16000;     // sampling frequency
    private int               audioEncoding = 2;
    private int               nChannels = 16;
    private CaptureManager cm;
    private AudioEventListener listener;


    private class AudioEventListener extends DataChunkListener {
        public AudioEventListener(Object sender) {
            super(sender, bufferSize);
        }

        @Override
        public synchronized void processData(Object sender, Object[] data) {
            if (data.length > 0) {
                Log.d(AudioEventListener.class.getName(), "Received data from event");
                Message msg = handler.obtainMessage();
                msg.obj = data[data.length - 1];
                handler.sendMessage(msg);
            }
        }
    }
/*
need to receive a buffer from listener. Don't know how.

 */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BytePollingDataEvent data = (BytePollingDataEvent) msg.obj;
            buffer = new short[bufferSize];

            surfaceView.drawThread.setBuffer(buffer);

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            bufferSize = AudioRecord.getMinBufferSize(FS, nChannels, audioEncoding);
            cm = new CaptureManager(Feature.MICROPHONE_UNCOMPRESSED, MimeType.AUDIO_UNCOMPRESSED, getContentResolver());
            listener = new AudioEventListener(cm);
            cm.addListener(listener);
            cm.prepare();
            listener.startListening();
            cm.begin();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot start capture manager or listener!", Toast.LENGTH_LONG).show();
        }
        surfaceView = (CSurfaceView)findViewById(R.id.surfaceView);
        surfaceView.drawThread.setBuffer(buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }
    @Override
    protected void onStop() {
        super.onStop();
        cm.stop();
        listener.stopListening();
    }

    protected void onPause() {
        super.onPause();
        cm.removeListener(listener);
    }

    protected void onResume() {
        super.onResume();
        cm.addListener(listener);
    }


    public void captureSound(View v) {
        if (surfaceView.drawThread.soundCapture) {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(false);
            surfaceView.drawThread.segmentIndex = -1;
            surfaceView.drawThread.FFTComputed  = Boolean.valueOf(false);
        }
        else {
            surfaceView.drawThread.soundCapture = Boolean.valueOf(true);

        }
    }

    public void wakeUp (View view) {
        // Do something in response to button
        Intent intent = new Intent(this, WakeUpActivity.class);
        startActivity(intent);
    }
}
