package com.sana.android.plugin.data;

import android.net.Uri;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryData;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.hardware.Feature;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;

/**
 * Created by hanlin on 9/13/14.
 */
public class BinaryDataWithPollingEvent extends BinaryData {
    private Object sender;
    private InputStream inStream;
    private BytePollingDataEvent event;

    public BinaryDataWithPollingEvent(
            Feature source,
            MimeType type,
            Uri uriToData,
            Object sender,
            InputStream inStream,
            int packetSize
    ) throws FileNotFoundException, URISyntaxException {
        super(source, type, uriToData);
        this.sender = sender;
        this.inStream = inStream;
        this.event = new BytePollingDataEvent(sender, inStream, packetSize);
    }

    @Override
    public BytePollingDataEvent getEvent() { return this.event; }

    @Override
    public void dispose() throws InterruptedException {
        this.event.stopEvent();
    }
}
