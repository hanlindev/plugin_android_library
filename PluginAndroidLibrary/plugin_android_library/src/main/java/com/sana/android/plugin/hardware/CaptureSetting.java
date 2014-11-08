package com.sana.android.plugin.hardware;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private SensorManager sensorManager;
    private String outputFileName = null;
    private String fileExtention = null;
    private String tempFileName = null;
    private Integer recorderSampleRate;
    private Integer recorderChannels;
    private String outputFolderName = null;
    private File outputFolder;

    public static CaptureSetting defaultSetting(Feature source, MimeType type
    ) {
        CaptureSetting result = new CaptureSetting();
        result.setDefaultForType(type);
        result.setDefaultForFeature(source);
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
            case MICROPHONE_UNCOMPRESSED:
                this.audioSource = MediaRecorder.AudioSource.MIC;
                this.fileExtention = ".wav";
                this.outputFolderName = "UncompressedAudioRecorder";
                this.tempFileName = "record_temp.raw";
                this.recorderSampleRate = 44100;
                this.recorderChannels = AudioFormat.CHANNEL_IN_STEREO;
                this.audioEncoder = AudioFormat.ENCODING_PCM_16BIT;
                break;
            case BLUETOOTH_MICROPHONE:
                this.audioEncoder = MediaRecorder.AudioEncoder.AAC;
                this.audioSource = MediaRecorder.AudioSource.MIC;
                this.outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
                this.outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                this.outputFileName += "/audiorecord.3gp";
                break;
            case CAMERA_REAR:
                this.outputFolderName = "MyCameraApp";
                this.outputFolder = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                this.fileExtention = ".jpg";
                break;
            default:
                break;
        }
    }

    public void setDefaultForType(MimeType type) {
        switch (type) {
            case AUDIO:
                this.audioEncoder = MediaRecorder.AudioEncoder.AMR_NB;
                this.audioSource = MediaRecorder.AudioSource.MIC;
                this.outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
                this.outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                this.outputFileName += "/audiorecord.3gp";
                break;
            case IMAGE:
                this.outputFolderName = "MyCameraApp";
                this.outputFolder = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                this.fileExtention = ".jpg";
                break;
            default:
                break;

        }
    }

    public Integer getAudioEncoder() {
        return this. audioEncoder;
    }


    public CaptureSetting setAudioEncoder(Integer audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public Integer getRecorderSampleRate() { return this.recorderSampleRate; }

    public CaptureSetting setRecorderSampleRate(Integer recorderSampleRate){
        this.recorderSampleRate = recorderSampleRate;
        return this;
    }

    public Integer getRecorderChannels() {return this.recorderChannels; }

    public CaptureSetting setRecorderChannels(Integer recorderChannels){
        this.recorderChannels = recorderChannels;
        return this;
    }

    public String getFileExtention() { return this.fileExtention; }

    public CaptureSetting setFileExtention(String fileExtention){
        this.fileExtention = fileExtention;
        return this;
    }

    public String getOutputFolderName() { return this.outputFolderName; }

    public CaptureSetting setOutputFolderName(String outputFolderName){
        this.outputFolderName = outputFolderName;
        return this;
    }

    public File getOutputFolder(){
        return this.outputFolder;
    }

    public CaptureSetting setOutputFolder(File outputFolder){
        this.outputFolder = outputFolder;
        return this;
    }

    public String getTempFileName() { return this.tempFileName; }

    public CaptureSetting setTempFileName(String tempFileName){
        this.tempFileName = tempFileName;
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

    public SensorManager getSensorManager() {
        return this.sensorManager;
    }

    public CaptureSetting setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        return this;
    }

}
