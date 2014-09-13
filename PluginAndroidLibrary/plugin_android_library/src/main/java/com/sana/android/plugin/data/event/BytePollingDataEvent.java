package com.sana.android.plugin.data.event;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hanlin on 9/13/14.
 * This is the event type that actively polls data from sensors and notifies
 * interested listeners.
 */

public class BytePollingDataEvent extends BaseDataEvent implements Runnable {
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

    public BytePollingDataEvent(Object sender, InputStream incomingDataChannel) {
        this(sender, incomingDataChannel, BytePollingDataEvent.BUFFER_SIZE_SMALL);
    }

    public BytePollingDataEvent(
            Object sender, InputStream incomingDataChannel, int bufferSize) {
        super(sender);
        this.incomingDataChannel = incomingDataChannel;
        this.bufferSize = bufferSize;
        this.pollingThreads = Executors.newSingleThreadExecutor();
        this.pointer = 0;
        this.pollingThreads.submit(this);
    }

    @Override
    public void run() {
        int numBytesRead = 0;// A value of -1 indicates closed stream.
        while (numBytesRead >= 0) {
            try {
                numBytesRead = this.incomingDataChannel.read(
                        this.buffer, this.pointer, this.bufferSize - this.pointer);
                this.pointer += numBytesRead;
                if (this.pointer == 0) {
                    this.notifyListeners(ArrayUtils.toObject(this.buffer));
                }
            } catch (IOException e) {
                // TODO decide what to do when the input stream is shutdown
                // unexpectedly. But I think we shouldn't fatal this.
                Log.d(
                        BytePollingDataEvent.LOG_TAG,
                        BytePollingDataEvent.UNEXPECTED_END_STREAM_EXCEPTION_MSG,
                        e
                );
                break;
            }
        }
    }
}
