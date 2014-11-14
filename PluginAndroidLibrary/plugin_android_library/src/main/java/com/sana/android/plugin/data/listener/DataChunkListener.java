package com.sana.android.plugin.data.listener;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This is the base listener that listens for updates by a fixed number of
 * data. It works by having a background thread monitoring new data
 * coming in. The listener has a fixed sized buffer that holds the incoming
 * data. When the buffer is full, the listener will process the data in
 * the buffer and clear it, allowing more data to be filled into the buffer.
 *
 * @author Han Lin
 */
public abstract class DataChunkListener
        implements DataListener, Runnable {
    private final static String LOG_TAG = "DataChunkListener";
    private final static String RESOURCE_LEAK_WARNING_MSG =
            "Threads not properly shutdown. Potential resource leak may be caused.";
    private final static String SHUTDOWN_INTERRUPTED_MSG_EXCEPTION_MSG =
        "Listener shutdown interrupted.";
    private final static String FATAL_INTERRUPTION_MSG_FORMAT =
        "Unable to carry out critical operation - %s";
    private final static String PUT_BYTE_OPERATION_NAME =
        "register incoming data.";

    private final static int SHUTDOWN_TIMEOUT = 5;
    private final static TimeUnit SHUTDOWN_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private ArrayBlockingQueue<Object> buffer;
    private LinkedBlockingQueue<DataAppender> appendOperations;
    private boolean isListening;
    private ExecutorService receiverThread;
    private Object sender;

    /**
     * @param bufferSize   The size of the temporary buffer. This is also
     *                     the interval of update.
     */
    public DataChunkListener(Object sender, int bufferSize) {
        this.sender = sender;
        this.buffer = new ArrayBlockingQueue<Object>(bufferSize, true);
        this.appendOperations = new LinkedBlockingQueue<DataAppender>();
        this.isListening = false;
        this.receiverThread = Executors.newSingleThreadExecutor();
    }

    public void setExpectedSender(Object sender) {
        this.sender = sender;
    }

    public Object getExpectedSender() {
        return this.sender;
    }

    @Override
    public synchronized void putData(Object[] data) {
        Log.d(
                DataChunkListener.LOG_TAG,
                "Received data of length " + data.length
        );
        if (this.isListening) {
            try {
                this.appendOperations.put(new DataAppender(this.buffer, data));
                Log.d(
                        DataChunkListener.LOG_TAG,
                        "DataAppender created for " + data
                );
            } catch (InterruptedException e) {
                // This is designed to wait indefinitely before the new data
                // are registered. If it is interrupted, we can assume
                // that something really bad happened, e.g. not enough memory.
                throw new Error(String.format(
                    DataChunkListener.FATAL_INTERRUPTION_MSG_FORMAT,
                    DataChunkListener.PUT_BYTE_OPERATION_NAME
                ));
            }
        }
    }

    @Override
    public void startListening() {
        this.isListening = true;
        this.receiverThread.submit(this);
    }

    @Override
    public void stopListening() {
        this.stopListening(
            DataChunkListener.SHUTDOWN_TIMEOUT,
            DataChunkListener.SHUTDOWN_TIMEOUT_UNIT
        );
    }

    @Override
    public void stopListening(long timeout, TimeUnit unit) {
        Log.d(
                DataChunkListener.LOG_TAG,
                "Stopping listener - " + this
        );
        this.isListening = false;
        this.receiverThread.shutdown();
        try {
            if (!this.receiverThread.awaitTermination(timeout,unit)) {
                Log.d(
                        DataChunkListener.LOG_TAG,
                        DataChunkListener.RESOURCE_LEAK_WARNING_MSG
                );
            }
        } catch (InterruptedException e) {
            Log.d(
                    DataChunkListener.LOG_TAG,
                    DataChunkListener.SHUTDOWN_INTERRUPTED_MSG_EXCEPTION_MSG,
                    e
            );
        }
        this.processRemainingData();
    }

    /**
     * The procedure the background thread uses to monitor incoming data.
     * When data is available, it will be appended to the buffer.
     * Process the data when the buffer is full and clear the buffer after
     * that.
     */
    @Override
    public void run() {
        while (this.isListening || !this.appendOperations.isEmpty()) {
            DataAppender appendOperation = null;
            try {
                appendOperation = this.appendOperations.take();
                Log.d(
                        DataChunkListener.LOG_TAG,
                        "AppendOperation " + appendOperation + " taken"
                );
                this.processAppendOperation(appendOperation);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.processRemainingData();
            }
        }
    }

    private void processAppendOperation(DataAppender appendOperation) {
        while (!appendOperation.run()) {
            this.processData(this.sender, this.buffer.toArray());
            this.buffer.clear();
        }
    }

    private void processRemainingData() {
        DataAppender[] appenders = this.appendOperations.toArray(new DataAppender[0]);
        for (DataAppender appender : appenders) {
            this.processAppendOperation(appender);
        }

        if (!this.buffer.isEmpty()) {
            this.processData(this.sender, this.buffer.toArray(new Byte[0]));
        }
    }

    /**
     * @param data  When invoked by the incoming data monitoring thread the number of data
     *                is less than or equal to the buffer size.
     */
    @Override
    public abstract void processData(Object sender, Object[] data);
}
