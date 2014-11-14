package com.sana.android.plugin.data;

import android.net.Uri;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.BytePushingDataEvent;
import com.sana.android.plugin.hardware.Feature;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * A binary data class that incorporates BytePushingEvent
 */
public class BinaryDataWithPushingEvent extends BinaryData {
    private InputStream inStream;
    private BytePushingDataEvent event;
    private Object sender;

    /**
     * @param source    The system feature by which the data is captured.
     * @param type    The MimeType of the data.
     * @param uriToData    The Uri to the binary file storing the data.
     * @param sender
     * @param inStream
     * @param packetSize   The size of data packet to expect. Be cautious
     *                     while setting this parameter. The event will
     *                     always try to obtain the specified number of
     *                     bytes from the input stream. If fewer bytes are
     *                     available in the stream, it will block.
     * @throws FileNotFoundException
     * @throws URISyntaxException
     */
    public BinaryDataWithPushingEvent(
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
        this.event = new BytePushingDataEvent(sender, inStream, packetSize);
    }

    @Override
    public BytePushingDataEvent getEvent() {
        return this.event;
    }

    @Override
    public void dispose() throws InterruptedException {
        this.event.stopEvent();
    }
}
