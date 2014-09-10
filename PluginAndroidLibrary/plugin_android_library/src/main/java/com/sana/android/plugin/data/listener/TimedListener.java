package com.sana.android.plugin.data.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Han Lin
 */
public abstract class TimedListener implements BinaryDataListener, Runnable {
    private long interval;
    private TimeUnit timeUnit;
    private ScheduledExecutorService scheduledThread;
    /**
     * @param interval  Is the time interval in milliseconds.
     */
    public TimedListener(long interval, TimeUnit unit) {
        this.interval = interval;
        this.timeUnit = unit;
        this.scheduledThread = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {

    }

    @Override
    public abstract void updateBytes(Byte[] bytes);
}
