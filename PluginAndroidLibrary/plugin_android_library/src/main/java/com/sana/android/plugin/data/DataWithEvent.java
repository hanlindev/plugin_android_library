package com.sana.android.plugin.data;

import com.sana.android.plugin.data.event.BaseDataEvent;

/**
 * Created by hanlin on 9/13/14.
 */
public interface DataWithEvent {

    /**
     * Get the event instance associated with this data.
     *
     * @return The event object.
     */
    public BaseDataEvent getEvent();

    /**
     * Dispose resources used
     *
     * @throws Exception
     */
    public void dispose() throws Exception;
}
