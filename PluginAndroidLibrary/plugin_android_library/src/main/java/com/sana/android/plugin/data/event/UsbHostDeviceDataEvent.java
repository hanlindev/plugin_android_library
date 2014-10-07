package com.sana.android.plugin.data.event;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by quang on 10/7/14.
 */
public class UsbHostDeviceDataEvent extends BaseDataEvent implements Runnable {

    private ExecutorService notificationMasterThread;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpoint;
    private int bufferSize;
    private byte[] buffer;
    private int timeout;

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

    @Override
    public void run() {
        buffer = new byte[bufferSize];
        connection.bulkTransfer(endpoint, buffer, buffer.length, timeout);
        notifyListeners(ArrayUtils.toObject(buffer));
    }
}
