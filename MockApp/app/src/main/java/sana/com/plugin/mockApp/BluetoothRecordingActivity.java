package sana.com.plugin.mockApp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.*;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.sana.android.plugin.hardware.BluetoothDevice;
import com.sana.android.plugin.hardware.CaptureSetting;

public class BluetoothRecordingActivity extends Activity {
    private static final String TAG = "AudioRecordTest";
    private BluetoothDevice BD;
    private AudioManager mAudioManager;
    @Override
    public void onCreate(Bundle icicle) {
        BD = new BluetoothDevice();
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
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mRecordButton.setEnabled(!isChecked); // Set checked state
                onPlayPressed(isChecked);  // Start/stop playback
            }
        });

        // Get AudioManager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d(TAG, "Audio SCO state: " + state);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    // now the connection has be established to the bluetooth device
                    unregisterReceiver(this);
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
        Log.d(TAG, "starting bluetooth");
        mAudioManager.startBluetoothSco();
    }

    // Toggle recording
    private void onRecordPressed(boolean shouldStartRecording) {
        if(shouldStartRecording)
            BD.begin();
        else
            BD.stop();
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
