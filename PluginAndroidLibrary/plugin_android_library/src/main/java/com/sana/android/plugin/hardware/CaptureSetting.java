package com.sana.android.plugin.hardware;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.sana.android.plugin.communication.MimeType;

/**
 * You should add your the capture setting required by your sensor. The setter
 * should return this instance. See the existing methods for reference.
 */
public class CaptureSetting {
    private Integer audioEncoder;
    private Integer audioSource;
    private Integer outputFormat;
    private Integer videoEncoder;
    private Integer videoSource;
    private ContentResolver contentResolver;
    private Context applicationContext;
    private static String outputFileName = null;

    public static CaptureSetting defaultSetting(Feature source, MimeType type
    ) {
        CaptureSetting result = new CaptureSetting();
        result.setDefaultForFeature(source);
        result.setDefaultForType(type);
        return result;
    }

    public void setDefaultForFeature(Feature source) {
        switch (source) {
            case MICROPHONE:
                this.audioEncoder = MediaRecorder.AudioEncoder.AAC;
                this.audioSource = MediaRecorder.AudioSource.MIC;
                this.outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
                this.outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                this.outputFileName += "/audiorecord.3gp";
                break;
            case BLUETOOTH_MICROPHONE:
                this.audioEncoder = MediaRecorder.AudioEncoder.AAC;
                this.audioSource = MediaRecorder.AudioSource.MIC;
                this.outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
                this.outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                this.outputFileName += "/audiorecord.3gp";
                break;
            default:
                break;
        }
    }

    public void setDefaultForType(MimeType type) {
        // placeholder
        this.audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
        this.audioSource = MediaRecorder.AudioSource.MIC;
        this.outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
        this.outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.outputFileName += "/audiorecord.3gp";
    }

    public Integer getAudioEncoder() {
        return this. audioEncoder;
    }

    public CaptureSetting setAudioEncoder(Integer audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public String getOutputFileName() { return this.outputFileName; }

    public CaptureSetting setOutputFilename(String filename) {
        this.outputFileName = filename;
        return this;
    }

    public Integer getAudioSource() {
        return audioSource;
    }

    public CaptureSetting setAudioSource(Integer audioSource) {
        this.audioSource = audioSource;
        return this;
    }



    public Integer getOutputFormat() {
        return outputFormat;
    }

    public CaptureSetting setOutputFormat(Integer outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public Integer getVideoEncoder() {
        return videoEncoder;
    }

    public CaptureSetting setVideoEncoder(Integer videoEncoder) {
        this.videoEncoder = videoEncoder;
        return this;
    }

    public Integer getVideoSource() {
        return videoSource;
    }

    public CaptureSetting setVideoSource(Integer videoSource) {
        this.videoSource = videoSource;
        return this;
    }

    public ContentResolver getContentResolver() {
        return this.contentResolver;
    }

    public CaptureSetting setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        return this;
    }

    public Context getApplicationContext() {
        return this.applicationContext;
    }

    public CaptureSetting setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }
}
