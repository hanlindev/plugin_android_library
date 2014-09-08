package com.sana.android.plugin.application;

/**
 * Created by Han Lin on 9/8/2014.
 */
public class CaptureObserveConfiguration {
    /**
     * This is the number of bytes to be buffered before they are notified to
     * the observer.
     */
    private long interval;

    public CaptureObserveConfiguration setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    public long getInterval() {
        return this.interval;
    }
}
