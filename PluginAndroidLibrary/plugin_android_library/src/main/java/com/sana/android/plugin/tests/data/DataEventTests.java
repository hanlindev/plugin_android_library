package com.sana.android.plugin.tests.data;

import android.net.LocalSocket;
import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;

import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.event.BytePollingDataEvent;
import com.sana.android.plugin.data.event.BytePushingDataEvent;
import com.sana.android.plugin.data.listener.DataChunkListener;
import com.sana.android.plugin.data.listener.DataListener;
import com.sana.android.plugin.data.listener.TimedListener;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanlin on 9/19/14.
 */
public class DataEventTests extends InstrumentationTestCase {
    private static final int TEST_DATA_SIZE = 1024;// Bytes
    private static final int TEST_LISTENER_BUFFER_SIZE_SMALL = 8;
    private static final long TEST_LISTENER_INTERVAL_SMALL = 1;
    private static final TimeUnit TEST_LISTENER_TIME_UNIT = TimeUnit.SECONDS;
    // This is the number of seconds we are going to wait for incoming data
    // from the byteReceiver before we fail the test.
    private static final long TEST_FAILURE_TIMEOUT = 5;
    private static final TimeUnit TEST_FAILURE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private InputStream is;
    private OutputStream os;
    private LocalSocket outSocket;
    // This socket receives data from outSocket and send it back
    private LocalSocket loopBackSocket;
    private Byte[] testData;
    private ArrayBlockingQueue<Byte> byteReceiver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.outSocket= new LocalSocket();
        this.is = this.outSocket.getInputStream();
        this.os = this.outSocket.getOutputStream();
        this.loopBackSocket = new LocalSocket();
        this.outSocket.connect(this.loopBackSocket.getLocalSocketAddress());
        this.loopBackSocket.connect(this.outSocket.getLocalSocketAddress());

        this.testData = this.getDataToBeWritten(DataEventTests.TEST_DATA_SIZE);
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

    public void testDataChunkListenerWithPollingEvent() throws IOException {
        testEventAndListener(
                this.getPollingEvent(), this.getDataChunkListener());
    }

    public void testTimedListenerWithPollingEvent() throws IOException {
        testEventAndListener(
                this.getPollingEvent(), this.getTimedListener());
    }

    public void testDataCunkListenerWithPushingEvent() throws IOException {
        testEventAndListener(
                this.getPushingEvent(), this.getDataChunkListener()
        );
    }

    public void testTimedListenerWithPushingEvent() throws IOException {
        testEventAndListener(
                this.getPushingEvent(), this.getTimedListener()
        );
    }

    public void testEventAndListener(BaseDataEvent event, DataListener listener
    ) throws IOException {
        event.addListener(listener);
        this.writeOutTestData();
        this.verifyData();
        listener.stopListening();
    }

    private void writeOutTestData() throws IOException {
        this.os.write(ArrayUtils.toPrimitive(this.testData));
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

class DataChunkListenerForTest extends DataChunkListener {
    private DataEventTests testCase;

    public DataChunkListenerForTest(
            DataEventTests testCase, int bufferSize) {
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

class TimedListenerForTest extends TimedListener {
    private DataEventTests testCase;

    public TimedListenerForTest(DataEventTests testCase, long interval, TimeUnit unit) {
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
