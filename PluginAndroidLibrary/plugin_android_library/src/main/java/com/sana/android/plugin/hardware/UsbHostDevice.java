package com.sana.android.plugin.hardware;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.UsbHostDeviceDataWithEvent;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by quang on 9/16/14.
 */
public class UsbHostDevice extends UsbGeneralDevice {

    private static final String LOG_TAG = "UsbHostDevice";

    private UsbDevice device;
    private ParcelFileDescriptor deviceFileDescriptor;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint endpoint;
    private int bufferSize;
    private int timeout;

    private UsbHostDeviceDataWithEvent dataWithEvent;
    private CaptureSetting setting;


    public UsbHostDevice(Context context) {
        super(context);
        this.bufferSize = 1;
        this.timeout = 0;
    }

    public UsbHostDevice(Context context, int bufferSize, int timeout) {
        super(context);
        this.bufferSize = bufferSize;
        this.timeout = timeout;
    }

    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (device != null) return;
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openDevice(device);
                    } else {
                        Log.d(UsbHostDevice.LOG_TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private void openDevice(UsbDevice device) {
        if (device == null) {
            Log.d(UsbHostDevice.LOG_TAG, "null device");
            return;
        }

        usbInterface = device.getInterface(1);
        connection = usbManager.openDevice(device);
        if (!connection.claimInterface(usbInterface, true)) {

            Log.d(UsbHostDevice.LOG_TAG, "Cannot claim interface");
            connection.close();
            return;
        }

        connection.controlTransfer(0x21, 0x22, 0, 0, null, 0, 0);

        // Set baud rate to be 9600
        connection.controlTransfer(0x21, 0x20, 0, 0,
                new byte[]{(byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08}, 7, 0);

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {

            if (usbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                    endpoint = usbInterface.getEndpoint(i);
                }
            }
        }

        Log.d(UsbHostDevice.LOG_TAG, "Opening new endpoint = " + endpoint + " at connection = " + connection);
    }

    private void closeDevice() {
        if (connection != null) {
            connection.releaseInterface(usbInterface);
        }
        device = null;
        deviceFileDescriptor = null;
    }

    @Override
    public DataWithEvent prepare() {

        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter deviceFilter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbBroadcastReceiver, deviceFilter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(UsbHostDevice.LOG_TAG, "Number of UsbHostDevice(s) found: " + deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        device = null;
        connection = null;
        if (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            Log.d(UsbHostDevice.LOG_TAG, "UsbHostDevice connected: " + device);
        }

        if (device == null) {
            Log.d(UsbHostDevice.LOG_TAG, "No devices found");
            return null;
        }

        openDevice(device);
        usbManager.requestPermission(device, intent);

        if (!usbManager.hasPermission(device)) {
            Log.d(UsbHostDevice.LOG_TAG, "Permission denied for device " + device);
            return null;
        }

        try {
            dataWithEvent = new UsbHostDeviceDataWithEvent(
                    Feature.USB_HOST,
                    MimeType.TEXT_PLAIN,
                    null,
                    this,
                    connection,
                    endpoint,
                    bufferSize,
                    timeout
            );
        } catch (FileNotFoundException e) {
            Log.d(UsbHostDevice.LOG_TAG, "file not found: " + e.toString());
        } catch (URISyntaxException e) {
            Log.d(UsbHostDevice.LOG_TAG, "uri exception: " + e.toString());
        }

        return dataWithEvent;

    }

    @Override
    public void begin() {
        if (device != null) {
            dataWithEvent.getEvent().startEvent();
        }
    }

    @Override
    public void stop() {
        Log.d(UsbHostDevice.LOG_TAG, "Stopping device");
        if (dataWithEvent != null && dataWithEvent.getEvent() != null) {
            try {
                dataWithEvent.getEvent().stopEvent();
            } catch (InterruptedException e) {
                Log.d(UsbHostDevice.LOG_TAG, "Interrupted exception: " + e.toString());
            }
        }
        closeDevice();
        Log.d(UsbHostDevice.LOG_TAG, "Unregistering Receiver");
        context.unregisterReceiver(usbBroadcastReceiver);
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) { this.setting = setting; }
}