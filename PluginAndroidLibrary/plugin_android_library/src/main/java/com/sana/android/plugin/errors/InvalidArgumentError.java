package com.sana.android.plugin.errors;

/**
 * Created by mashiro on 10/3/2014.
 */
public class InvalidArgumentError extends Error {
    public InvalidArgumentError(String message) {
        super("Invalid argument error: " + message);
    }
}
