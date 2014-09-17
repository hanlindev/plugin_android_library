package com.sana.android.plugin.communication;

/**
 * Created by hanlin on 9/12/14.
 */
public enum MimeType {
    TEXT("text/plain");


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
