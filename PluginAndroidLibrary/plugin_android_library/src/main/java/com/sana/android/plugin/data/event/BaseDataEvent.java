package com.sana.android.plugin.data.event;

import android.util.Log;

import com.sana.android.plugin.data.listener.DataListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanlin on 9/12/14.
 */
public abstract class BaseDataEvent {
    private static final String LOG_TAG = "BaseDataEvent";
    private Vector<DataListener> listeners;
    private ExecutorService notificationThread;
    private Object sender;

    public BaseDataEvent(Object sender) {
        this.sender = sender;
        this.listeners = new Vector<DataListener>();
        this.notificationThread = Executors.newCachedThreadPool();
    }

    public Object getSender() {
        return this.sender;
    }

    public void notifyListeners(final Object[] data) {
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

    /**
     * Add a listener to the event. You can add the same listener
     * object multiple times but it will only be notified once.
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

    public abstract void startEvent();
    public abstract void stopEvent() throws InterruptedException;
}
