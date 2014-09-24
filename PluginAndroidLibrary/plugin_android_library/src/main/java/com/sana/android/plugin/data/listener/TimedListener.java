package com.sana.android.plugin.data.listener;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Han Lin
 */
public abstract class TimedListener implements Runnable, DataListener {
    private final static String LOG_TAG =
            "TimedListener";
    private final static String SHUTDOWN_INTERRUPTED_EXCEPTION_MSG =
            "Timed listener shutdown interrupted.";
    private final static String FATAL_INTERRUPTION_MSG_FORMAT =
            "Unable to carry out critical operation - %s";
    private final static String PUT_BYTE_OPERATION_NAME =
            "register incoming data.";

    private final static int SHUTDOWN_TIMEOUT = 5;
    private final static TimeUnit SHUTDOWN_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private long interval;
    private TimeUnit timeUnit;
    private ScheduledExecutorService scheduledThread;
    private LinkedBlockingQueue<Object> buffer;
    private Object sender;

    public TimedListener() {}

    /**
     * @param interval  Is the time interval in milliseconds.
     */
    public TimedListener(Object sender, long interval, TimeUnit unit) {
        this.sender = sender;
        this.interval = interval;
        this.timeUnit = unit;
        this.scheduledThread = Executors.newSingleThreadScheduledExecutor();
        this.buffer = new LinkedBlockingQueue<Object>();
    }

    public void setExpectedSender(Object sender) {
        this.sender = sender;
    }

    public Object getExpectedSender() {
        return this.sender;
    }

    @Override
    public void run() {
        try {
            int currentSize = this.buffer.size();
            Object[] bufferedObjects = new Object[currentSize];
            for (int i = 0; i < currentSize; ++i) {
                    bufferedObjects[i] = this.buffer.take();
            }
            this.processData(this.sender, bufferedObjects);
        } catch (InterruptedException e) {
            this.processRemainingData();
        }
    }

    private void processRemainingData() {
        Object[] remaining = this.buffer.toArray(new Object[0]);
        this.processData(this.sender, remaining);
    }

    @Override
    public synchronized void putData(Object[] data) {
        Log.d(
                TimedListener.LOG_TAG,
                "Received data of length " + data.length
        );
        for (Object element : data) {
            try {
                this.buffer.put(element);
            } catch (InterruptedException e) {
                // This is designed to wait indefinitely before the new data
                // are registered. If it is interrupted, we can assume
                // that something really bad happened, e.g. not enough memory.
                throw new Error(String.format(
                        TimedListener.FATAL_INTERRUPTION_MSG_FORMAT,
                        TimedListener.PUT_BYTE_OPERATION_NAME
                ));
            }
        }
    }

    @Override
    public void startListening() {
        this.scheduledThread.scheduleAtFixedRate(
                this, this.interval, this.interval, this.timeUnit);
    }

    @Override
    public void stopListening() {
        this.stopListening(
                TimedListener.SHUTDOWN_TIMEOUT,
                TimedListener.SHUTDOWN_TIMEOUT_UNIT
        );
    }

    @Override
    public void stopListening(long timeout, TimeUnit unit) {
        this.scheduledThread.shutdown();
        try {
            if (!this.scheduledThread.awaitTermination(timeout,unit)) {
                // TODO decide what to do when shutdown times out.
            }
        } catch (InterruptedException e) {
            // TODO decide what to do when shutdown is interrupted.
            Log.d(
                    TimedListener.LOG_TAG,
                    TimedListener.SHUTDOWN_INTERRUPTED_EXCEPTION_MSG,
                    e
            );
        }
    }

    @Override
    public abstract void processData(Object sender, Object[] data);
}
