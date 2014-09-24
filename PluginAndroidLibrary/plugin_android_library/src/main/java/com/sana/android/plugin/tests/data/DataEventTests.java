package com.sana.android.plugin.tests.data;

import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;

import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.data.event.BytePushingDataEvent;
import com.sana.android.plugin.data.listener.DataChunkListener;
import com.sana.android.plugin.data.listener.DataListener;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.tests.mocks.MockListener;
import com.sana.android.plugin.tests.subjects.DataChunkListenerForTest;
import com.sana.android.plugin.tests.subjects.TimedListenerForTest;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanlin on 9/19/14.
 */
public class DataEventTests extends InstrumentationTestCase {
    private static final int TEST_DATA_SIZE = 8;// Bytes
    private static final int TEST_LISTENER_BUFFER_SIZE_SMALL = 8;
    private static final long TEST_LISTENER_INTERVAL_SMALL = 1;
    private static final TimeUnit TEST_LISTENER_TIME_UNIT = TimeUnit.SECONDS;
    // This is the number of seconds we are going to wait for incoming data
    // from the byteReceiver before we fail the test.
    private static final long TEST_FAILURE_TIMEOUT = 5;
    private static final TimeUnit TEST_FAILURE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private InputStream is;
    // This socket receives data from outSocket and send it back
    private Byte[] testData;
    private ArrayBlockingQueue<Byte> byteReceiver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.testData = this.getDataToBeWritten(DataEventTests.TEST_DATA_SIZE);
        this.is = new ByteArrayInputStream(
                ArrayUtils.toPrimitive(this.testData));

        this.byteReceiver = new ArrayBlockingQueue<Byte>(
                DataEventTests.TEST_DATA_SIZE, true);
    }

    private DataChunkListener getDataChunkListener() {
        return new DataChunkListenerForTest(
                this, DataEventTests.TEST_LISTENER_BUFFER_SIZE_SMALL);
    }

    private TimedListener getTimedListener() {
        return new TimedListenerForTest(
                this,
                DataEventTests.TEST_LISTENER_INTERVAL_SMALL,
                DataEventTests.TEST_LISTENER_TIME_UNIT
        );
    }

    private BytePollingDataEvent getPollingEvent() {
        return new BytePollingDataEvent(
                this, this.is, BytePollingDataEvent.BUFFER_SIZE_SMALL);
    }

    private BytePushingDataEvent getPushingEvent() {
        return new BytePushingDataEvent(
                this, this.is, BytePushingDataEvent.UNKNOWN_PACKET_SIZE);
    }

    public void testPushingEvent() {
        this.testEventAndListener(
                this.getPushingEvent(), new MockListener(this));
    }

    public void testPollingEvent() {
        this.testEventAndListener(
                this.getPollingEvent(), new MockListener(this));
    }

    public void testDataChunkListener() {

    }

    public void testEventAndListener(BaseDataEvent event, DataListener listener
    ) {
        event.addListener(listener);
        event.startEvent();
        if (event instanceof BytePushingDataEvent) {
            try {
                while (this.is.available() > 0) {
                    ((BytePushingDataEvent) event).bytesAvailable();
                }
            } catch (IOException e) {
                fail("Exception thrown while reading data from input stream.");
            }
        }
        this.verifyData();
        listener.stopListening();
    }

    private Byte[] getDataToBeWritten(int dataLength) {
        Byte[] result = new Byte[dataLength];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) (i % Byte.MAX_VALUE);
        }
        return result;
    }

    private void verifyData() {
        Byte[] receivedData = new Byte[this.testData.length];
        try {
            int pointer = 0;
            while (pointer < receivedData.length) {
                Byte currentByte = this.byteReceiver.poll(
                        DataEventTests.TEST_FAILURE_TIMEOUT,
                        DataEventTests.TEST_FAILURE_TIMEOUT_UNIT
                );
                receivedData[pointer++] = currentByte;
            }
            MoreAsserts.assertEquals(
                    "Listener received wrong data from event.",
                    this.testData,
                    receivedData
            );
        } catch (InterruptedException e) {
            fail("The listener failed to receive any data from the event.");
        }
    }

    public void putData(Byte[] data) throws InterruptedException {
        for (Byte item: data) {
            this.byteReceiver.put(item);
        }
    }
}
