package com.sana.android.plugin.data.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This is the base listener that listens for updates by a fixed number of
 * bytes. It works by having a background thread monitoring new bytes
 * coming in. The listener has a fixed sized buffer that holds the incoming
 * bytes. When the buffer is full, the listener will process the bytes in
 * the buffer and clear it, allowing more bytes to be filled into the buffer.
 *
 * @author Han Lin
 */
public abstract class DataChunkListener
    implements BinaryDataListener, Runnable {

    private final static String FATAL_INTERRUPTION_MSG_FORMAT =
        "Unable to carry out critical operation - %s";

    private final static String PUT_BYTE_OPERATION_NAME =
        "register incoming bytes.";

    private final static int SHUTDOWN_TIMEOUT = 5;
    private final static TimeUnit SHUTDOWN_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private ArrayBlockingQueue<Byte> buffer;
    private LinkedBlockingQueue<ByteAppender> appendOperations;
    private boolean isListening;
    private ExecutorService receiverThread;

    /**
     * @param bufferSize   The size of the temporary buffer. This is also
     *                     the interval of update.
     */
    public DataChunkListener(int bufferSize) {
        this.buffer = new ArrayBlockingQueue<Byte>(bufferSize, true);
        this.appendOperations = new LinkedBlockingQueue<>();
        this.isListening = false;
        this.receiverThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Put the given bytes in the waiting list of bytes to be processed.
     *
     * @param bytes    The bytes to be processed.
     * @throws InterruptedException when the bytes fail to be put into the
     * waiting list.
     */
    @Override
    public synchronized void putBytes(Byte[] bytes) {
        if (this.isListening) {
            try {
                this.appendOperations.put(new ByteAppender(this.buffer, bytes));
            } catch (InterruptedException e) {
                // This is designed to wait indefinitely before the new bytes
                // are registered. If it is interrupted, we can assume
                // that something really bad happened, e.g. not enough memory.
                throw new Error(String.format(
                    DataChunkListener.FATAL_INTERRUPTION_MSG_FORMAT,
                    DataChunkListener.PUT_BYTE_OPERATION_NAME
                ));
            }
        }
    }

    /**
     * Start the background thread and begin accepting bytes.
     */
    @Override
    public void startListening() {
        this.isListening = true;
        this.receiverThread.submit(this);
    }

    /**
     * Attempt to stop this data listener. It will wait for 5 seconds before
     * timing out.
     */
    @Override
    public void stopListening() {
        this.stopListening(
            DataChunkListener.SHUTDOWN_TIMEOUT,
            DataChunkListener.SHUTDOWN_TIMEOUT_UNIT
        );
    }
    /**
     * Stop accepting more bytes, then signal the monitoring thread to finish
     * the remaining job.
     */
    @Override
    public void stopListening(long timeout, TimeUnit unit) {
        this.isListening = false;
        this.receiverThread.shutdown();
        try {
            if (!this.receiverThread.awaitTermination(timeout,unit)) {
                // TODO decide what to do when shutdown times out.
            }
        } catch (InterruptedException e) {
            // TODO decide what to do when shutdown is interrupted.
        }
    }

    /**
     * The procedure the background thread uses to monitor incoming bytes.
     * Process the bytes when the buffer is full and clear the buffer after
     * that.
     */
    @Override
    public void run() {
        while (this.isListening || !this.appendOperations.isEmpty()) {
            ByteAppender appendOperation = null;
            try {
                appendOperation = this.appendOperations.take();
                this.processAppendOperation(appendOperation);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.processRemainingData();
            }
        }
    }

    private void processAppendOperation(ByteAppender appendOperation) {
        while (!appendOperation.run()) {
            this.updateBytes(this.buffer.toArray(new Byte[0]));
            this.buffer.clear();
        }
    }

    private void processRemainingData() {
        ByteAppender[] appenders = this.buffer.toArray(new ByteAppender[0]);
        for (ByteAppender appender : appenders) {
            this.processAppendOperation(appender);
        }

        if (!this.buffer.isEmpty()) {
            this.updateBytes(this.buffer.toArray(new Byte[0]));
        }
    }

    /**
     * Process the new bytes.
     *
     * @param bytes   This can be any number of bytes. But when invoked by the
     *                incoming bytes monitoring thread the number of bytes
     *                is less than or equal to the buffer size.
     */
    @Override
    public abstract void updateBytes(Byte[] bytes);
}
