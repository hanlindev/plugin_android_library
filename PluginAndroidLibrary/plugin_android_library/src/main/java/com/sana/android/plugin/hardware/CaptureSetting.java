package com.sana.android.plugin.hardware;

import android.media.MediaRecorder;

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
    }

<<<<<<< HEAD
    public MediaRecorder.AudioEncoder getAudioEncoder() {
=======
    public Integer getAudioEncoder() {
>>>>>>> f67962040df0491a96f788164bf22ab28639d642
        return audioEncoder;
    }

    public CaptureSetting setAudioEncoder(Integer audioEncoder) {
        this.audioEncoder = audioEncoder;
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
}
