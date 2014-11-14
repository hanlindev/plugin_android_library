package com.sana.android.plugin.hardware;

import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.event.BytePollingDataEvent;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Mia on 23/9/14.
 *
 * This AudioRecordDevice class supports recording .3gp format files.
 * It also includes a MediaPlayer to play the recording.
 */
public class AudioRecordDevice implements GeneralDevice {
    private ContentResolver resolver;
    private static String mFileName = "";
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private static final String LOG_TAG = "AudioRecord";
    private static final String DEFAULT_FILE_NAME = "/audiorecordtest.3gp";
    private static final String PREPARE_FAILED = "prepare() failed.";
    private int audioSource = -1;
    private int audioEncoder = -1;
    private int outputFormat = -1;

    public AudioRecordDevice(){
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
                + DEFAULT_FILE_NAME;
    }

    private void ensureFileExists(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }
/*
 This method prepares the device and return a DataWithEvent for further
 process of the obtained data.
 */
    @Override
    public DataWithEvent prepare() {
        mRecorder = new MediaRecorder();
        mRecorder.setOutputFile(mFileName);
        try {
            ensureFileExists(mFileName);
            InputStream is = new FileInputStream(mFileName);
            DataWithEvent result = new BinaryDataWithPollingEvent(
                    Feature.MICROPHONE,
                    MimeType.AUDIO,
                    CommManager.getInstance().getUri(),
                    this,
                    is,
                    BytePollingDataEvent.BUFFER_SIZE_SMALL);
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void begin() {
        if (mRecorder != null && audioSource != -1) {
            mRecorder.setAudioSource(audioSource);
            mRecorder.setOutputFormat(outputFormat);
            mRecorder.setAudioEncoder(audioEncoder);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, PREPARE_FAILED);
            }
            mRecorder.start();
        }
    }

    @Override
    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        moveData();
    }

    private void moveData() {
        try {
            if(CommManager.getInstance().getUri() == null) {
                return ;
            }
            Log.d(
                    LOG_TAG,
                    CommManager.getInstance().getUri().toString()
            );
            FileInputStream is = new FileInputStream(mFileName);
            OutputStream os = resolver.openOutputStream(
                    CommManager.getInstance().getUri());
            Log.e(LOG_TAG,
                    CommManager.getInstance().getUri().toString());
            IOUtils.copy(is, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void pauseRecorder(){
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void pausePlayer(){
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void setCaptureSetting(CaptureSetting setting) {
        this.audioEncoder = setting.getAudioEncoder();
        this.audioSource = setting.getAudioSource();
        this.outputFormat = setting.getOutputFormat();
        this.resolver = setting.getContentResolver();
        this.mFileName = setting.getOutputFileName();
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, PREPARE_FAILED);
        }
    }

    public void stopPlaying() {
        if (mPlayer.isPlaying())
            mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    public MediaRecorder getmRecorder(){
        return mRecorder;
    }

    public MediaPlayer getmPlayer(){
        return mPlayer;
    }

}
