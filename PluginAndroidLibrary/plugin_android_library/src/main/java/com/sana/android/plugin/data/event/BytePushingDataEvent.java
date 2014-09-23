package com.sana.android.plugin.data.event;

import android.util.Log;

import com.sana.android.plugin.errors.UnsupportedDeviceError;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hanlin on 9/13/14.
 * This event is actively triggered by sensors when the sensor pushes
 * data to the app. Therefore we don't need to have complicated
 * routines. It will get notified when data is available and
 * read whatever available in the input stream.
 */
public class BytePushingDataEvent extends BaseDataEvent {
    public static final int UNKNOWN_PACKET_SIZE = -1;

    private static final String LOG_TAG = "IEL.BytePushingDataEvent";
    private static final String READ_BYTE_EXCEPTION_MSG =
            "Error reading bytes from input stream.";

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

    public synchronized void bytesAvailable() {
        try {
            int numBytesToRead =
                    (this.packetSize != BytePushingDataEvent.UNKNOWN_PACKET_SIZE)
                            ? this.incomingData.available() : this.packetSize;
            Byte[] data = this.readBytes(numBytesToRead);
            this.notifyListeners(data);
        } catch (IOException e) {
            // TODO handle IOException
            Log.d(
                    BytePushingDataEvent.LOG_TAG,
                    BytePushingDataEvent.READ_BYTE_EXCEPTION_MSG,
                    e
            );
        }
    }

    private Byte[] readBytes(int numBytesToRead) throws IOException {
        byte[] result = new byte[numBytesToRead];
        int pointer = 0;
        while (pointer <= numBytesToRead) {
            int currentSize = numBytesToRead - pointer;
            this.incomingData.read(result, pointer, currentSize);
        }
        return ArrayUtils.toObject(result);
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
