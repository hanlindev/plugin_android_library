package com.sana.android.plugin.tests.subjects;

import com.sana.android.plugin.data.listener.DataChunkListener;
import com.sana.android.plugin.tests.data.DataEventAndListenerTests;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by DIdiHL on 9/24/2014.
 */
public class DataChunkListenerForTest extends DataChunkListener {
    private DataEventAndListenerTests testCase;

    public DataChunkListenerForTest(
            DataEventAndListenerTests testCase, int bufferSize) {
        super(testCase, bufferSize);
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

