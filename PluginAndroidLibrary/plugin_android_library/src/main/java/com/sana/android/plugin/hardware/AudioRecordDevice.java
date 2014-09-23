package com.sana.android.plugin.hardware;

import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import com.sana.android.plugin.data.DataWithEvent;
import java.io.IOException;
import java.io.File;

/**
 * Created by Mia on 23/9/14.
 */
public class AudioRecordDevice implements GeneralDevice {
    private ContentResolver resolver;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static final String LOG_TAG = "AudioRecord";
    private int audioEncoder;
    private int audioSource;
    private int outputFormat;

    public AudioRecordDevice(){
        prepare();
    }

    public AudioRecordDevice(CaptureSetting setting){
        setCaptureSetting(setting);
        prepare();
    }

    @Override
    public DataWithEvent prepare() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecord.3gp";
        return null;
    }

    @Override
    public void begin() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(audioSource);
        mRecorder.setOutputFormat(outputFormat);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(audioEncoder);

        //mRecorder.set(mic,three_gpp,mfilename,amr_nb);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }

    @Override
    public void stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    @Override
    public void reset() {
        stop();
        File file = new File(mFileName);
        boolean deleted = file.delete();
        if(deleted){
            mFileName = null;
        }

    }

    public void pause(){
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

    }

    @Override
    public void setCaptureSetting(CaptureSetting setting) {
        this.audioEncoder = setting.getAudioEncoder();
        this.audioSource = setting.getAudioSource();
        this.outputFormat = setting.getOutputFormat();

    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }


}
