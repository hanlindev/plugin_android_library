package com.sana.android.plugin.tests.hardware;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.test.InstrumentationTestCase;

import com.sana.android.plugin.data.event.UsbHostDeviceDataEvent;
import com.sana.android.plugin.hardware.UsbHostDevice;
import static org.mockito.Mockito.*;

/**
 * Created by quang on 10/28/14.
 */
public class UsbHostDeviceTest extends InstrumentationTestCase {

    private UsbHostDeviceDataEvent dataEvent;
    private static final int BUFFER_SIZE = 1000000;
    private static final int TEST_VALUE = 20;

    protected void setUp() {
        UsbDeviceConnection mockConnection = mock(UsbDeviceConnection.class);
        when(mockConnection.bulkTransfer(null, new byte[BUFFER_SIZE], BUFFER_SIZE, 0)).thenReturn(TEST_VALUE);
        dataEvent = new UsbHostDeviceDataEvent(this, mockConnection, null, 1, 0);
        dataEvent.startEvent();
        while (true) {
            byte[] buffer = dataEvent.getBuffer();
            if (buffer.length != 0) {
                assertEquals(0, buffer.length % TEST_VALUE);
                break;
            }
        }
    }

    private void testStartEvent() {
        dataEvent.startEvent();
    }
}
