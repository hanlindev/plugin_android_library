package com.sana.android.plugin.hardware;

import android.media.MediaRecorder;

import com.sana.android.plugin.communication.MimeType;

/**
 * You should add your the capture setting required by your sensor. The setter
 * should return this instance. See the existing methods for reference.
 */
public class CaptureSetting {
    private MediaRecorder.AudioEncoder audioEncoder;
    private MediaRecorder.AudioSource audioSource;
    private MediaRecorder.OutputFormat outputFormat;
    private MediaRecorder.VideoEncoder videoEncoder;
    private MediaRecorder.VideoSource videoSource;

    public static CaptureSetting defaultSetting(Feature source, MimeType type
    ) {
        CaptureSetting result = new CaptureSetting();
        CaptureSetting.setDefaultForFeature(source);
        CaptureSetting.setDefaultForType(type);
        return result;
    }

    public static void setDefaultForFeature(Feature source) {
        // TODO implement
    }

    public static void setDefaultForType(MimeType type) {
        // TODO implement
    }

    public MediaRecorder.AudioEncoder getAudioEncoder() {
        return audioEncoder;
    }

    public CaptureSetting setAudioEncoder(MediaRecorder.AudioEncoder audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public MediaRecorder.AudioSource getAudioSource() {
        return audioSource;
    }

    public CaptureSetting setAudioSource(MediaRecorder.AudioSource audioSource) {
        this.audioSource = audioSource;
        return this;
    }

    public MediaRecorder.OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public CaptureSetting setOutputFormat(MediaRecorder.OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public MediaRecorder.VideoEncoder getVideoEncoder() {
        return videoEncoder;
    }

    public CaptureSetting setVideoEncoder(MediaRecorder.VideoEncoder videoEncoder) {
        this.videoEncoder = videoEncoder;
        return this;
    }

    public MediaRecorder.VideoSource getVideoSource() {
        return videoSource;
    }

    public CaptureSetting setVideoSource(MediaRecorder.VideoSource videoSource) {
        this.videoSource = videoSource;
        return this;
    }
}
