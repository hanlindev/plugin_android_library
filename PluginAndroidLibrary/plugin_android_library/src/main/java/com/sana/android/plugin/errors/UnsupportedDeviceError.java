package com.sana.android.plugin.errors;

import com.sana.android.plugin.hardware.Feature;

/**
 * Created by Chen Xi on 9/22/2014.
 */
public class UnsupportedDeviceError extends InvalidInvocationError {
    private static final String MESSAGE_FORMAT = "Unsupported device feature - %s";
    public UnsupportedDeviceError(Feature feature) {
        super(String.format(MESSAGE_FORMAT, feature.toCommonName()));
    }
}
