package com.sana.android.plugin.data;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.net.Uri;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.data.event.UsbHostDeviceDataEvent;
import com.sana.android.plugin.hardware.Feature;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * Created by quang on 10/7/14.
 */
public class UsbHostDeviceDataWithEvent extends BinaryData {
    private Object sender;
    private InputStream inStream;
    private UsbHostDeviceDataEvent event;

    public UsbHostDeviceDataWithEvent(
            Feature source,
            MimeType type,
            Uri uriToData,
            Object sender,
            UsbDeviceConnection connection,
            UsbEndpoint endpoint,
            int bufferSize,
            int timeout
    ) throws FileNotFoundException, URISyntaxException {
        super(source, type, uriToData);
        this.sender = sender;
        this.inStream = inStream;
        this.event = new UsbHostDeviceDataEvent(sender, connection, endpoint, bufferSize, timeout);
    }

    @Override
    public UsbHostDeviceDataEvent getEvent() { return this.event; }

    @Override
    public void dispose() throws InterruptedException {
        this.event.stopEvent();
    }
}
