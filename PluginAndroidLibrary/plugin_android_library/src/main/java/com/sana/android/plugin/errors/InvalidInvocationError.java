package com.sana.android.plugin.errors;

/**
 * Created by hanlin on 9/14/14.
 */
public class InvalidInvocationError extends Error {
    private static final String MESSAGE_PREFIX = "Invalid invocation: ";
    public InvalidInvocationError(String msg) {
        super(InvalidInvocationError.MESSAGE_PREFIX + msg);
    }
}
