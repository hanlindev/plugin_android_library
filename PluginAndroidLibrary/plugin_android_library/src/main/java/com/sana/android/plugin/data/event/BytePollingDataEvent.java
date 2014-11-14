package com.sana.android.plugin.data.event;

import android.util.Log;

import com.sana.android.plugin.data.BinaryDataWithPollingEvent;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the event type that actively polls data from sensors and notifies
 * interested listeners. When the buffer of this event is full,
 * {@link #notifyListeners()} will be called.
 *
 * @author Han Lin
 */

public class BytePollingDataEvent extends BaseDataEvent implements Runnable {

    private static final long TERMINATION_TIMEOUT = 5;
    private static final TimeUnit TERMINATION_TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final String LOG_TAG = "IEL.BytePollingDataEvent";
    private static final String UNEXPECTED_END_STREAM_EXCEPTION_MSG =
            "Unexpected end of input stream encountered.";

    public static final int BUFFER_SIZE_SINGLE = 1;
    public static final int BUFFER_SIZE_VERY_SMALL = 8;
    public static final int BUFFER_SIZE_SMALL = 16;
    public static final int BUFFER_SIZE_MEDIUM = 32;
    public static final int BUFFER_SIZE_LARGE = 64;

    private InputStream incomingDataChannel;
    private int bufferSize;
    private byte[] buffer;
    private ExecutorService pollingThreads;
    private int pointer;

    /**
     * @param sender
     * @param incomingDataChannel  This is the input stream from which the event
     *                                 will poll data from.
     */
    public BytePollingDataEvent(Object sender, InputStream incomingDataChannel) {
        this(sender, incomingDataChannel, BytePollingDataEvent.BUFFER_SIZE_SMALL);
    }

    /**
     * @param sender                The source of data.
     * @param incomingDataChannel   The input stream from which the event polls
     *                                  data from.
     * @param bufferSize            The size of the buffer. The event will only
     *                              notify listeners when its buffer becomes
     *                              full.
     */
    public BytePollingDataEvent(
            Object sender, InputStream incomingDataChannel, int bufferSize) {
        super(sender);
        this.incomingDataChannel = incomingDataChannel;
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
        this.pollingThreads = Executors.newSingleThreadExecutor();
        this.pointer = 0;
    }

    public void startEvent() {
        this.pollingThreads.submit(this);
    }

    public void stopEvent() throws InterruptedException {
        this.pollingThreads.shutdown();
        this.pollingThreads.awaitTermination(
                BytePollingDataEvent.TERMINATION_TIMEOUT,
                BytePollingDataEvent.TERMINATION_TIMEOUT_UNIT
        );
    }

    @Override
    public void run() {
        int numBytesRead = 0;// A value of -1 indicates closed stream.
        while (numBytesRead >= 0) {
            try {
                numBytesRead = this.incomingDataChannel.read(
                        this.buffer, this.pointer, this.bufferSize - this.pointer);
                this.pointer = (this.pointer + numBytesRead) % this.bufferSize;
                Log.d(
                        BytePollingDataEvent.LOG_TAG,
                        String.format(
                                "Number of bytes read: %d and current" +
                                        " pointer position: %d",
                                numBytesRead,
                                this.pointer
                        )
                );

                if (this.pointer == 0) {
                    this.notifyListeners();
                }
            } catch (IOException e) {
                Log.d(
                        BytePollingDataEvent.LOG_TAG,
                        BytePollingDataEvent.UNEXPECTED_END_STREAM_EXCEPTION_MSG,
                        e
                );
                break;
            }
        }

        // Notify the listeners of remaining bytes.
        if (this.pointer > 0) {
            this.notifyListeners();
        }
    }

    private void notifyListeners() {
        this.notifyListeners(ArrayUtils.toObject(this.buffer));
    }
}
