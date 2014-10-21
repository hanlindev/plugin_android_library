package com.example.mia.snorelab;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    public  CSurfaceView   	surfaceView;
    private SoundSampler   	soundSampler;
    public  static short[]  buffer;
    public  static int      bufferSize;     // in bytes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            soundSampler = new SoundSampler(this);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot instantiate SoundSampler", Toast.LENGTH_LONG).show();
        }

        try {
            soundSampler.init();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Cannot initialize SoundSampler.", Toast.LENGTH_LONG).show();
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


}
