package com.sana.android.plugin.data.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Han Lin
 */
public abstract class TimedListener implements BinaryDataListener, Runnable {
    private final static String FATAL_INTERRUPTION_MSG_FORMAT =
            "Unable to carry out critical operation - %s";

    private final static String PUT_BYTE_OPERATION_NAME =
            "register incoming bytes.";

    private final static int SHUTDOWN_TIMEOUT = 5;
    private final static TimeUnit SHUTDOWN_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private long interval;
    private TimeUnit timeUnit;
    private ScheduledExecutorService scheduledThread;
    private LinkedBlockingQueue<Byte> buffer;
    /**
     * @param interval  Is the time interval in milliseconds.
     */
    public TimedListener(long interval, TimeUnit unit) {
        this.interval = interval;
        this.timeUnit = unit;
        this.scheduledThread = Executors.newSingleThreadScheduledExecutor();
        this.buffer = new LinkedBlockingQueue<Byte>();
    }

    @Override
    public void run() {
        try {
            int currentSize = this.buffer.size();
            Byte[] bufferedBytes = new Byte[currentSize];
            for (int i = 0; i < currentSize; ++i) {
                    bufferedBytes[i] = this.buffer.take();
            }
            this.updateBytes(bufferedBytes);
        } catch (InterruptedException e) {
            this.processRemainingData();
        }
    }

    private void processRemainingData() {
        Byte[] remaining = this.buffer.toArray(new Byte[0]);
        this.updateBytes(remaining);
    }

    @Override
    public synchronized void putBytes(Byte[] bytes) {
        for (Byte element : bytes) {
            try {
                this.buffer.put(element);
            } catch (InterruptedException e) {
                // This is designed to wait indefinitely before the new bytes
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
        }
    }

    @Override
    public abstract void updateBytes(Byte[] bytes);
}
