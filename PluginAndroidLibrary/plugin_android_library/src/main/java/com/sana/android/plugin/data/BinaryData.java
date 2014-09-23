package com.sana.android.plugin.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.style.AlignmentSpan;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.hardware.Feature;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Han Lin on 9/10/14.
 */
public abstract class BinaryData implements DataWithEvent {
    private Feature source;
    private MimeType type;
    private Uri uriToData;

    public BinaryData(Feature source, MimeType type, Uri uriToData)
            throws URISyntaxException, FileNotFoundException {
        this.source = source;
        this.type = type;
        this.uriToData = uriToData;
    }

    public Feature getSource() {
        return this.source;
    }

    public MimeType getMimeType() {
        return this.type;
    }

    /**
     * Get the URI to the file that stores the binary data.
     *
     * @return The URI of the file.
     */
    public Uri getUriToData() {
        return this.uriToData;
    }

    /**
     * Get the data that was captured.
     *
     * @param contentResolver    The ContentResolver instance of the app. You
     *                           can use getContentResolver() static method
     *                           to get the instance.
     * @return The byte array containing the captured data.
     * @throws IOException
     */
    public byte[] getBinaryData(ContentResolver contentResolver
    ) throws IOException {
        InputStream is = contentResolver.openInputStream(this.uriToData);
        return IOUtils.toByteArray(is);
    }

    /**
     * @param contentResolver    The ContentResolver instance of the app. You
     *                           can use getContentResolver() static method
     *                           to get the instance.
     * @return
     * @throws IOException
     */
    public String getStringData(ContentResolver contentResolver
    ) throws IOException {
        return new String(this.getBinaryData(contentResolver));
    }

    /**
     * Get the binary data that was captured from the given start position
     * with the given number of bytes.
     *
     * @param start     Is the start position of the binary data.
     * @param length    Is the number of bytes to read from the start position.
     * @return The requested portion of binary data.
     */
    public Byte[] getBinaryData(int start, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public abstract BaseDataEvent getEvent();
}
