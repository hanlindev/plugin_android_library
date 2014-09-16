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
 * Created by hanlin on 9/13/14.
 */
public abstract class BinaryDataWithPushingEvent extends BinaryData {
    private InputStream inStream;
    private BytePushingDataEvent event;
    private Object sender;

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
}
