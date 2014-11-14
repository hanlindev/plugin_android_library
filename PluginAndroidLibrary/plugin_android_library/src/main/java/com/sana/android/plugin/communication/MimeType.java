package com.sana.android.plugin.communication;

/**
 * Created by hanlin on 9/12/14.
 */
public enum MimeType {
    TEXT_PLAIN("text/plain"),
    TEXT_CSV("text/csv"),
    IMAGE("image/jpeg"),
    AUDIO("audio/3gpp"),
    AUDIO_UNCOMPRESSED("audio/amr"),
    VIDEO("video/3gpp"),
    JSON("application/json"),
    XML("application/xml");


    private String value;

    private MimeType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static MimeType fromString(String value) {
        for (MimeType type: MimeType.values()) {
            if (type.toString().equals(value)) {
                return type;
            }
        }
        return null;
    }

}
