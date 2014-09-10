package com.sana.android.plugin.data;

import android.net.Uri;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.Feature;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Han Lin on 9/10/14.
 */
public class BinaryData {
    private Feature source;
    private MimeType type;
    private Uri uriToData;

    public BinaryData(Feature source, MimeType type, Uri uriToData) {
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
     * @return The byte array containing the captured data.
     */
    public byte[] getBinaryData() throws URISyntaxException, IOException {
        URI javaUri = new URI(this.uriToData.toString());
        return IOUtils.toByteArray(new FileInputStream(new File(javaUri)));
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
}
