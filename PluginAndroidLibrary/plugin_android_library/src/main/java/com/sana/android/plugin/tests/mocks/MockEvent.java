package com.sana.android.plugin.tests.mocks;

import android.util.Log;

import com.sana.android.plugin.data.event.BaseDataEvent;

import java.util.ArrayList;

/**
 * Created by DIdiHL on 9/25/2014.
 */
public class MockEvent extends BaseDataEvent {
    private static final String LOG_TAG = "MockEvent";
    private static final long WAIT_TIMEOUT = 100;
    private ArrayList<Byte[]> dataSet;

    public MockEvent(Object sender, ArrayList<Byte[]> dataSet) {
        super(sender);
        this.dataSet = dataSet;
    }

    @Override
    public void startEvent() {
        for (Byte[] data: this.dataSet) {
            super.notifyListeners(data);
            try {
                this.wait(MockEvent.WAIT_TIMEOUT);
            } catch (InterruptedException e) {
                Log.e(
                        MockEvent.LOG_TAG,
                        "MockEvent is interrupted while waiting for the next" +
                                " notification",
                        e
                );
            }
        }
    }

    @Override
    public void stopEvent() throws InterruptedException {
    }
}
