package com.sana.android.plugin.tests.hardware;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.sana.android.plugin.data.event.UsbHostDeviceDataEvent;
import com.sana.android.plugin.data.listener.TimedListener;
import com.sana.android.plugin.hardware.UsbHostDevice;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Created by quang on 10/28/14.
 */
public class UsbHostDeviceTest extends InstrumentationTestCase {

    private UsbHostDeviceDataEvent dataEvent;
    private static final int BUFFER_SIZE = 1000000;
    private static final int TEST_VALUE = 10;
    private static final byte[] TEST_BUFFER_ARRAY = new byte[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
    private byte[] receivedBuffer;
    private int receivedLength;

    private class TestListener extends TimedListener {

        Object sender;

        public TestListener(Object sender, long interval, TimeUnit unit) {
            super(sender, interval, unit);
            this.setExpectedSender(sender);
        }

        @Override
        public void setExpectedSender(Object sender) {
            this.sender = sender;
        }

        @Override
        public void processData(Object sender, Object[] data) {
            if (this.sender != sender) return;

            if (data.length > 0) {
                if (receivedLength == -1) receivedLength = 0;
                receivedLength += data.length;
                receivedBuffer = new byte[receivedLength];
                for (int i = 0; i < TEST_VALUE; i++) {
                    receivedBuffer[i] = (byte)data[i];
                }
            }
        }
    }
    protected void setUp() {
        // Setting up dexmaker cache
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        UsbDeviceConnection mockConnection = mock(UsbDeviceConnection.class);
        doAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                byte[] temp = (byte[]) invocation.getArguments()[1];
                for (int i = 0; i < TEST_VALUE; i++) {
                    temp[i] = TEST_BUFFER_ARRAY[i];
                }
                return TEST_VALUE;
            }
        }).when(mockConnection).bulkTransfer(null, new byte[BUFFER_SIZE], BUFFER_SIZE, 0);

        receivedBuffer = null;
        receivedLength = -1;

        TestListener listener = new TestListener(this, 1, TimeUnit.MILLISECONDS);
        listener.startListening();

        dataEvent = new UsbHostDeviceDataEvent(this, mockConnection, null, TEST_VALUE, 0);
        dataEvent.addListener(listener);
    }

    public void testStartEvent() throws InterruptedException {
        dataEvent.startEvent();
        while (true) {
            if (receivedLength != -1) {
                assertEquals(0, receivedLength % TEST_VALUE);
                assertNotNull(receivedBuffer);
                for (int i = 0; i < TEST_VALUE; i++) {
                    assertEquals(TEST_BUFFER_ARRAY[i], receivedBuffer[i]);
                }
                break;
            }
        }
        dataEvent.stopEvent();
    }
}
