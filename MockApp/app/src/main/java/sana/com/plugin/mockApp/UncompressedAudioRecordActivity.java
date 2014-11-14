package sana.com.plugin.mockApp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.AudioRecordUncompressedDevice;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.Feature;

public class UncompressedAudioRecordActivity extends Activity {

    private CaptureManager cm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uncompressed_audio_record);

        setButtonHandlers();
        enableButtons(false);

        cm = new CaptureManager(
                Feature.MICROPHONE_UNCOMPRESSED,
                MimeType.AUDIO_UNCOMPRESSED,
                getContentResolver());
        cm.prepare();
    }

    private void setButtonHandlers() {
        ((Button)findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        ((Button)findViewById(R.id.btnStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id,boolean isEnable){
        ((Button)findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart,!isRecording);
        enableButton(R.id.btnStop,isRecording);
    }

    private void startRecording(){
        cm.begin();

    }

    private void stopRecording(){
        cm.stop();

    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnStart:{
                    AppLog.logString("Start Recording");

                    enableButtons(true);
                    startRecording();

                    break;
                }
                case R.id.btnStop:{
                    AppLog.logString("Start Recording");

                    enableButtons(false);
                    stopRecording();

                    break;
                }
            }
        }
    };
}
