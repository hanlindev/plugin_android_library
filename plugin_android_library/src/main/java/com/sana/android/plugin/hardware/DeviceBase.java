package com.sana.android.plugin.hardware;

import com.sana.android.plugin.application.CaptureObserveConfiguration;
import com.sana.android.plugin.application.DataConverter;

import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * Created by Han Lin on 9/9/2014.
 */
public abstract class DeviceBase<T> extends Observable {
    private Feature source;
    private CaptureObserveConfiguration observeConfig;
    Vector<Byte> byteBuffer;

    public DeviceBase() {
        this(null, null);
    }

    public DeviceBase(Feature source, CaptureObserveConfiguration observeConfig) {
        this.source = source;
        this.observeConfig = observeConfig;
        this.byteBuffer = new Vector<Byte>();
    }

    public abstract void prepare();
    public abstract void start();
    public abstract T stop(DataConverter<T> converter);
    public abstract T stop();
}
