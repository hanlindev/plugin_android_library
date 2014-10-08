package sana.com.plugin.mockApp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sana.android.plugin.application.CaptureManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.AudioRecordDevice;
import com.sana.android.plugin.hardware.Feature;

public class AudioRecordActivity extends ActionBarActivity {
//    private AudioRecordDevice audioRecord;
    private CaptureManager cm;

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
    private RecordButton mRecordButton = null;
    private PlayButton   mPlayButton = null;

    private void onRecord(boolean start) {
        if (start) {
            cm.begin();
        } else {
            cm.stop();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
//            audioRecord.startPlaying();
        } else {
//            audioRecord.stopPlaying();
        }
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

//        audioRecord = new AudioRecordDevice();
        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));

        cm = new CaptureManager(Feature.MICROPHONE, MimeType.AUDIO, getContentResolver());
        cm.prepare();
        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
//        audioRecord.pauseRecorder();
//        audioRecord.pausePlayer();
    }
}

