package com.sana.android.plugin.data;

import android.net.Uri;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryData;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.hardware.Feature;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;

/**
 * A binary data class that incorporates polling data event.
 *
 * @author Han Lin
 */
public class BinaryDataWithPollingEvent extends BinaryData {
    private Object sender;
    private InputStream inStream;
    private BytePollingDataEvent event;

    /**
     * @param source    The system feature by which the data is captured.
     * @param type    The MimeType of the data.
     * @param uriToData    The Uri to the binary file storing the data.
     * @param sender                The source of data.
     * @param inStream The input stream from which the event polls
     *                                  data from.
     * @param packetSize The size of the buffer. The event will only
     *                              notify listeners when its buffer becomes
     *                              full.
     * @throws FileNotFoundException when the file is not found at the given
     * location.
     * @throws URISyntaxException when the URI is malformed.
     */
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
