package com.sana.android.plugin.hardware;

import android.media.MediaRecorder;

import com.sana.android.plugin.communication.MimeType;

/**
 * You should add your the capture setting required by your sensor. The setter
 * should return this instance. See the existing methods for reference.
 */
public class CaptureSetting {
    private int audioEncoder;
    private int audioSource;
    private int outputFormat;
    private int videoEncoder;
    private int videoSource;

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


    public int getAudioEncoder() {
        return audioEncoder;
    }

    public CaptureSetting setAudioEncoder(int audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public CaptureSetting setAudioSource(int audioSource) {
        this.audioSource = audioSource;
        return this;
    }

    public int getOutputFormat() {
        return outputFormat;
    }

    public CaptureSetting setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public int getVideoEncoder() {
        return videoEncoder;
    }

    public CaptureSetting setVideoEncoder(int videoEncoder) {
        this.videoEncoder = videoEncoder;
        return this;
    }

    public int getVideoSource() {
        return videoSource;
    }

    public CaptureSetting setVideoSource(int videoSource) {
        this.videoSource = videoSource;
        return this;
    }
}
