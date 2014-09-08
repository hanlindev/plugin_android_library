package com.sana.android.plugin.application;

import java.util.Observable;
import com.sana.android.plugin.hardware.Feature;
import com.sana.android.plugin.application.DataConverter;

/**
 * Created by Han Lin on 9/8/2014.
 * The object being passed to the notifyObservers method will be byte array.
 */
public class CaptureManager extends Observable {
    private CaptureObserveConfiguration observeConfig;

    public CaptureManager(CaptureObserveConfiguration observeConfig) {
        this.observeConfig = observeConfig;
    }

    public void startCapture(Feature source) {
        // TODO implement
    }

    public <T> T stopCapture(Feature source, DataConverter<T> converter) {
        // TODO implement
        return null;
    }
}
