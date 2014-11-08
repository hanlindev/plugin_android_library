package com.sana.android.plugin.data.listener;

import java.util.concurrent.BlockingQueue;

/**
 * An instance of DataAppender represents an append operation. It is built with
 * a blocking queue that act as the buffer to append to together with the data
 * to append. Refer to {@link com.sana.android.plugin.data.listener.DataChunkListener}'s
 * source code for sample usage.
 *
 * This class doesn't inherently solve race condition. The user needs to make sure
 * that the list of DataAppender instances is in the correct order and they are
 * called in the same order.
 *
 * @author Han Lin
 */
public class DataAppender {
    private final static String NULL_ARGUMENT_ERROR_MSG =
        "Arguments can't be null";

    private Object sender;
    private BlockingQueue<Object> to;
    private Object[] from;
    // This is the position of the next byte to be transferred to 'to'.
    // It is used because the operation can be unsuccessful due to
    // 'to' being full.
    private int currentPosition;

    /**
     * After instantiation, the operation won't be carried out until the {@link #run()}
     * method is called.
     *
     * @param to     The destination buffer.
     * @param from   The data to append. Note that the array may contain the original
     *                 data objects. Do not modify them directly.
     */
    public DataAppender(BlockingQueue<Object> to, Object[] from) {
        if (to == null || from == null) {
            throw new Error(DataAppender.NULL_ARGUMENT_ERROR_MSG);
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
