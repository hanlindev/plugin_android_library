package com.sana.android.plugin.data.event;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by quang on 10/7/14.
 */
public class UsbHostDeviceDataEvent extends BaseDataEvent implements Runnable {

    private static final String LOG_TAG = "UsbHostDeviceDataEvent";
    private static final int BUFFER_SIZE = 1000000;

    private ExecutorService notificationMasterThread;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpoint;
    private byte[] buffer;
    private int bufferSize;
    private int timeout;
    private int pointer;

    public UsbHostDeviceDataEvent(
            Object sender,
            UsbDeviceConnection connection,
            UsbEndpoint endpoint,
            int bufferSize,
            int timeout
    ) {
        super(sender);
        this.notificationMasterThread = Executors.newSingleThreadExecutor();
        this.connection = connection;
        this.endpoint = endpoint;
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    @Override
    public void startEvent() {
        notificationMasterThread.submit(this);
    }

    @Override
    public void stopEvent() throws InterruptedException {
        notificationMasterThread.shutdown();
    }

    public void dispose() throws InterruptedException {
        stopEvent();
        buffer = null;
    }

    public byte[] getBuffer() {
        buffer = ArrayUtils.subarray(buffer, 0, pointer);
        return buffer;
    }

    @Override
    public void run() {
        buffer = new byte[bufferSize];
        pointer = 0;

        Log.d(UsbHostDeviceDataEvent.LOG_TAG, "Listening for endpoint = " + endpoint + " at connection = " + connection);

        while (true) {
            byte[] temp = new byte[BUFFER_SIZE];
            final int numBytesRead = connection.bulkTransfer(endpoint, temp, temp.length, 0);
            if (numBytesRead > 0) {
                Log.d(UsbHostDeviceDataEvent.LOG_TAG, "Number of bytes read: " + numBytesRead);
                for (int i = 0; i < numBytesRead && pointer < bufferSize; i++, pointer++)
                    buffer[pointer] = temp[i];
                if (pointer >= bufferSize) {
                    notifyListeners(ArrayUtils.toObject(buffer));
                    pointer = 0;
                }
            } else if (numBytesRead < 0) {
                Log.d(UsbHostDeviceDataEvent.LOG_TAG, "No more data to be read from the connection");
                buffer = ArrayUtils.subarray(buffer, 0, pointer);
                notifyListeners(ArrayUtils.toObject(buffer));
                break;
            }
        }
    }
}
