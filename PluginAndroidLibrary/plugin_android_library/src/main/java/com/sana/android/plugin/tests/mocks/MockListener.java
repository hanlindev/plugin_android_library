package com.sana.android.plugin.tests.mocks;

import android.util.Log;

import com.sana.android.plugin.data.listener.DataListener;
import com.sana.android.plugin.tests.data.DataEventAndListenerTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by DIdiHL on 9/24/2014.
 */
public class MockListener implements DataListener {
    private static final String LOG_TAG = "MockListener";

    private DataEventAndListenerTests dataEventAndListenerTests;
    public MockListener(DataEventAndListenerTests dataEventAndListenerTests) {
        this.dataEventAndListenerTests = dataEventAndListenerTests;
    }

    @Override
    public void startListening() {
    }

    @Override
    public void stopListening() {
    }

    @Override
    public void setExpectedSender(Object sender) {
    }

    @Override
    public Object getExpectedSender() {
        return this.dataEventAndListenerTests;
    }

    @Override
    public void stopListening(long timeout, TimeUnit unit) {
    }

    @Override
    public synchronized void putData(Object[] data) {
        Log.d(
                MockListener.LOG_TAG,
                "Received data with count of " + data.length
        );

        try {
            this.dataEventAndListenerTests.putData(new ArrayList<Object>(Arrays.asList(data)).toArray(new Byte[0]));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processData(Object sender, Object[] data) {
    }
}
