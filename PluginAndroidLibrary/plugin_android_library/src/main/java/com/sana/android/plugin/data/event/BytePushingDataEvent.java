package com.sana.android.plugin.data.event;

import android.util.Log;

import com.sana.android.plugin.errors.UnsupportedDeviceError;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by hanlin on 9/13/14.
 * This event is actively triggered by sensors when the sensor pushes
 * data to the app. Therefore we don't need to have complicated
 * routines. It will get notified when data is available and
 * read whatever available in the input stream.
 */
public class BytePushingDataEvent extends BaseDataEvent {
    public static final int UNKNOWN_PACKET_SIZE = -1;

    private static final String LOG_TAG = "BytePushingDataEvent";
    private static final String READ_BYTE_EXCEPTION_MSG =
            "Error reading bytes from input stream.";

    // This is the maximum number of attempts while trying to get data
    // from the input stream. The event will retry reading from the
    // stream if the specified number of bytes was not read.
    private static final int MAX_READING_ATTEMPT_COUNTS = 5;

    private InputStream incomingData;
    private int packetSize;

    /**
     * Create an instance of the event with a given packet size.
     *
     * @param sender
     * @param incomingData
     * @param packetSize   The size of data packet to expect. Be cautious
     *                     while setting this parameter. The event will
     *                     always try to obtain the specified number of
     *                     bytes from the input stream. If fewer bytes are
     *                     available in the stream, it will block.
     */
    public BytePushingDataEvent(
            Object sender, InputStream incomingData, int packetSize) {
        super(sender);
        this.incomingData = incomingData;
        this.packetSize = packetSize;
    }

    /**
     * Tells the event there is available data to be read. The event will
     * determine the number of bytes it needs to read and proceed on reading
     * them. After the event is done reading the bytes, it will return the
     * number of bytes read.
     * @return The number of bytes read. -1 if the stream is closed or
     * when there were errors while reading.
     */
    public synchronized int bytesAvailable() {
        try {
            int numBytesToRead =
                    (this.packetSize == BytePushingDataEvent.UNKNOWN_PACKET_SIZE)
                            ? this.incomingData.available() : this.packetSize;
            if (numBytesToRead == -1) {
                return -1;
            }

            Byte[] data = this.readBytes(numBytesToRead);
            this.notifyListeners(data);
            Log.d(
                BytePushingDataEvent.LOG_TAG,
                "Number of bytes read: " + data.length
            );
            return data.length;
        } catch (IOException e) {
            // TODO handle IOException
            Log.d(
                    BytePushingDataEvent.LOG_TAG,
                    BytePushingDataEvent.READ_BYTE_EXCEPTION_MSG,
                    e
            );
            return -1;
        }
    }

    /**
     * Read the specified number of bytes from the input stream.
     *
     * @param numBytesToRead    The number of bytes to be read from the input
     *                             stream. The method will attempt to read the
     *                             specified number of bytes a few times before
     *                             return the result.
     * @return The bytes read.
     * @throws IOException
     */
    private Byte[] readBytes(int numBytesToRead) throws IOException {
        byte[] buffer = new byte[numBytesToRead];
        int pointer = 0;
        int attemptCount = 0;
        while (pointer <= numBytesToRead &&
                attemptCount < BytePushingDataEvent.MAX_READING_ATTEMPT_COUNTS)
        {
            int currentSize = numBytesToRead - pointer;
            this.incomingData.read(buffer, pointer, currentSize);
            pointer = currentSize;
            ++attemptCount;
        }

        Byte[] result;
        if (pointer < buffer.length) {
            result = new Byte[pointer];
            System.arraycopy(buffer, 0, result, 0, pointer);
        } else {
            result = ArrayUtils.toObject(buffer);
        }

        return result;
    }

    @Override
    public void startEvent() {
        // No thread is used in this event, don't need to do anything to start
        // it.
    }

    @Override
    public void stopEvent() throws InterruptedException {
        // Don't need this either.
    }
}
