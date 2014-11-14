package com.sana.android.plugin.errors;

/**
 * Created by DIdiHL on 10/20/2014.
 */
public class InvalidCaptureSettingsError extends Error {
    private static final String ERROR_FORMAT =
            "Invalid CaptureSetting instance: %s";
    public InvalidCaptureSettingsError(String reason) {
        super(String.format(ERROR_FORMAT, reason));
    }

    public InvalidCaptureSettingsError(String reason, Throwable e) {
        super(String.format(ERROR_FORMAT, reason), e);
    }
}
