package com.sana.android.plugin.data.listener;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by hanlin on 9/10/14.
 */
public class DataAppender {
    private final static String NULL_ARGUMENT_ERROR_MSG =
        "Arguments can't be null";

    private Object sender;
    private ArrayBlockingQueue<Object> to;
    private Object[] from;
    // This is the position of the next byte to be transferred to 'to'.
    // It is used because the operation can be unsuccessful due to
    // 'to' being full.
    private int currentPosition;

    public DataAppender(ArrayBlockingQueue<Object> to, Object[] from) {
        this.sender = sender;
        if (to == null || from == null) {
            throw new Error(DataAppender.NULL_ARGUMENT_ERROR_MSG);
        }

        this.to = to;
        this.from = from;
        this.currentPosition = 0;
    }

    public Object getSender() {
        return this.sender;
    }

    public boolean run() {
        boolean successful = false;
        while (this.currentPosition < this.from.length) {
            successful = this.to.offer(this.from[this.currentPosition]);
            if (!successful) {
                break;
            }
            ++this.currentPosition;
        }
        return successful;
    }
}
