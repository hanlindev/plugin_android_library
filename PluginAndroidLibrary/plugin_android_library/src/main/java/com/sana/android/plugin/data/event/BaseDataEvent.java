package com.sana.android.plugin.data.event;

import android.util.Log;

import com.sana.android.plugin.data.listener.DataListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The base abstract data event class. It provides support for updating clients
 * new data as they are captured by the sensors. Concrete implementations should
 * override the {@link #startEvent()} and {@link #stopEvent()} methods and use
 * the {@link #notifyListeners(Object[])} methods to get the listeners notified.
 *
 * @author Han Lin
 */
public abstract class BaseDataEvent {
    private static final String LOG_TAG = "BaseDataEvent";
    private Vector<DataListener> listeners;
    private ExecutorService notificationThread;
    private Object sender;

    /**
     * The source of data. Although there is no restriction on who should be
     * the sender. In principle it should be one of the
     * {@link com.sana.android.plugin.hardware.GeneralDevice} implementations.
     *
     * @param sender
     */
    public BaseDataEvent(Object sender) {
        this.sender = sender;
        this.listeners = new Vector<DataListener>();
        this.notificationThread = Executors.newCachedThreadPool();
    }

    public Object getSender() {
        return this.sender;
    }

    /**
     * Notify the listeners of new data. Each listener will be notified
     * by threads in a cached thread pool.
     *
     * @param data
     */
    public void notifyListeners(final Object[] data) {
        if (data.length > 0) {
            HashSet<DataListener> addedListeners = new HashSet<DataListener>();
            for (final DataListener listener : this.listeners) {
                if (!addedListeners.contains(listener)) {
                    this.notificationThread.submit(new Runnable() {
                        @Override
                        public void run() {
                            listener.putData(data);
                        }
                    });
                    addedListeners.add(listener);
                }
            }
        }
    }

    /**
     * Add a listener to the event. You can add the same listener
     * object multiple times but it will only be notified once in
     * the event of an update.
     *
     * @param listener    The listener object.
     * @returns True if the given listener expects the same sender
     *          as the event or false.
     */
    public boolean addListener(DataListener listener) {
        boolean result = false;
        if (listener.getExpectedSender() == this.sender) {
            this.listeners.add(listener);
            result = true;
        }
        return result;
    }

    public void removeListener(DataListener listener) {
        ArrayList<DataListener> removing = new ArrayList<DataListener>();
        removing.add(listener);
        this.listeners.removeAll(removing);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    /**
     * Start the data event. The principle is that before this method
     * is called by the user, the listeners should never get any
     * data update; and after it is called and when some condition is
     * met, the listeners must get updated.
     */
    public abstract void startEvent();

    /**
     * After calling this method, the event should finish the current
     * notification task after which no more notifications should be
     * made.
     *
     * @throws Exception generally many data events are associated with
     * some IO resources so the caller should expect to catch exceptions.
     */
    public abstract void stopEvent() throws Exception;
}
