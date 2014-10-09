package sana.com.plugin.mockApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.Feature;

public class BluetoothRecordingActivity extends Activity {
    private CaptureManager cm;
    @Override
    public void onCreate(Bundle icicle) {
        //using the default capture setting
        //cs = new CaptureSetting();
        //cs.setDefaultForFeature(Feature.MICROPHONE);
        //BD = new BluetoothDevice(this, cs);

        super.onCreate(icicle);
        setContentView(R.layout.activity_bluetooth_recording);
        final ToggleButton mRecordButton = (ToggleButton) findViewById(R.id.record_button);
        final ToggleButton mPlayButton = (ToggleButton) findViewById(R.id.play_button);

        // Set up record Button
        mRecordButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mPlayButton.setEnabled(!isChecked); // Set checked state
                onRecordPressed(isChecked); // Start/stop recording
            }
        });
        cm = new CaptureManager(Feature.BLUETOOTH_MICROPHONE, MimeType.AUDIO, getContentResolver(),this);
        cm.prepare();

        // Set up play Button
        /*
        mPlayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mRecordButton.setEnabled(!isChecked); // Set checked state
                onPlayPressed(isChecked);  // Start/stop playback
            }
        });*/
    }

    // Toggle recording
    private void onRecordPressed(boolean shouldStartRecording) {
        if(shouldStartRecording)
            cm.begin();
        else
            cm.stop();
    }
    // Toggle playback
    /*
    private void onPlayPressed(boolean shouldStartPlaying) {
        if (shouldStartPlaying)
            BD.startPlaying();
        else
            BD.stopPlaying();
    }
    */
    /*
    public void onPause() {
        super.onPause();
        BD.pauseRecorder();
        BD.pausePlayer();
    }
    */
}
