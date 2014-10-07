package sana.com.plugin.mockApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.BluetoothDevice;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;
import com.sana.android.plugin.hardware.FeatureChecker;

public class BluetoothRecordingActivity extends Activity {
    private BluetoothDevice BD;
    private CaptureManager cm;

    @Override
    public void onCreate(Bundle icicle) {
//        BD = new BluetoothDevice(this);
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

        // Set up play Button
        mPlayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mRecordButton.setEnabled(!isChecked); // Set checked state
                onPlayPressed(isChecked);  // Start/stop playback
            }
        });
        //startBluetoothMic
//        BD.startBluetoothMic();
        this.cm = new CaptureManager(Feature.BLUETOOTH, MimeType.AUDIO, getContentResolver());
    }

    // Toggle recording
    private void onRecordPressed(boolean shouldStartRecording) {
        if(shouldStartRecording)
            cm.begin();
        else
            cm.stop();
    }
    // Toggle playback
    private void onPlayPressed(boolean shouldStartPlaying) {
        if (shouldStartPlaying)
            BD.startPlaying();
        else
            BD.stopPlaying();
    }

    public void onPause() {
        super.onPause();
        BD.pauseRecorder();
        BD.pausePlayer();
    }
}
