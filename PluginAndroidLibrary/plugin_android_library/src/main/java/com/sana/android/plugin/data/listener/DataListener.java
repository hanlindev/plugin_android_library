package com.sana.android.plugin.data.listener;

import java.util.concurrent.TimeUnit;

/**
 * The interface that can be added to {@link com.sana.android.plugin.data.event.BaseDataEvent}
 * implementations.
 *
 * @author Han Lin
 */
public interface DataListener {
    /**
     * Called by the library user after the app is ready to receive data from the event.
     */
    public void startListening();

    /**
     * Called by the library user after the app no longer needs data updates. This moethod or
     * {@link #stopListening(long, java.util.concurrent.TimeUnit)} must be called in order
     * to prevent resource leak.
     */
    public void stopListening();

    /**
     * Called by the library user to tell the event from which object this listener
     * is expecting data updates.This should always be one of the {@link com.sana.android.plugin.hardware.GeneralDevice}
     * implementations. So if you are using {@link com.sana.android.plugin.application.CaptureManager},
     * the sender should be <code>captureManager.getDevice();</code>
     *
     * @param sender    The source of data update.
     */
    public void setExpectedSender(Object sender);

    /**
     * Get the expected sender object set by {@link #setExpectedSender(Object)}.
     * @return The expected sender set by {@link #setExpectedSender(Object)}.
     */
    public Object getExpectedSender();

    /**
     * Called by the library user after the app no longer needs data updates.
     * Explicit timeout is needed in case the disposal of resources takes
     * too much time.
     *
     * @param timeout
     * @param unit
     */
    public void stopListening(long timeout, TimeUnit unit);

    /**
     * Called by the data event to put data into the listener. This is the notification
     * from the event. Note that the data passed in will be the original data object.
     * Therefore you should never modify the object itself in case there are other listeners
     * receiving the same data. Make a copy if you really need to make some changes to it.
     *
     * @param data    The data update.
     */
    public void putData(Object[] data);

    /**
     * Called by the library user to process the given data.
     *
     * @param sender   The source of the data.
     * @param data     The given data.
     */
    public void processData(Object sender, Object[] data);
}
