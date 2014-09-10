package com.sana.android.plugin.data.listener;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by hanlin on 9/10/14.
 */
public class ByteAppender {
    private final static String NULL_ARGUMENT_ERROR_MSG =
        "Arguments can't be null";
    private ArrayBlockingQueue<Byte> to;
    private Byte[] from;
    // This is the position of the next byte to be transferred to 'to'.
    // It is used because the operation can be unsuccessful due to
    // 'to' being full.
    private int currentPosition;

    public ByteAppender(ArrayBlockingQueue<Byte> to, Byte[] from) {
        if (to == null || from == null) {
            throw new Error(ByteAppender.NULL_ARGUMENT_ERROR_MSG);
        }

        this.to = to;
        this.from = from;
        this.currentPosition = 0;
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
