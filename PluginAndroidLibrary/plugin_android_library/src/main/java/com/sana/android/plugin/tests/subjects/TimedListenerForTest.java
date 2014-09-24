package com.sana.android.plugin.tests.subjects;

import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.tests.data.DataEventAndListenerTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by DIdiHL on 9/24/2014.
 */
public class TimedListenerForTest extends TimedListener {
    private DataEventAndListenerTests testCase;

    public TimedListenerForTest(DataEventAndListenerTests testCase, long interval, TimeUnit unit) {
        super(testCase, interval, unit);
        this.testCase = testCase;
    }

    @Override
    public void processData(Object sender, Object[] data) {
        Byte[] byteData = new ArrayList<Object>(Arrays.asList(data))
                .toArray(new Byte[0]);
        try {
            this.testCase.putData(byteData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
