package com.sana.android.plugin.hardware;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.UsbHostDeviceDataWithEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

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

    private UsbHostDeviceDataWithEvent dataWithEvent;
    private CaptureSetting setting;

    public UsbHostDevice(Context context) {
        super(context);
    }

    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            openDevice(device);
                        }
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

        usbInterface = device.getInterface(0);
        endpoint = usbInterface.getEndpoint(0);
        connection = usbManager.openDevice(device);
        connection.claimInterface(usbInterface, true);
        //deviceInput = new FileInputStream();
        //deviceFileDescriptor = connection.getFileDescriptor();
        //connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
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
        IntentFilter accessoryFilter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbBroadcastReceiver, accessoryFilter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.d(UsbHostDevice.LOG_TAG, "Number of UsbHostDevice(s) found: " + deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            Log.d(UsbHostDevice.LOG_TAG, "UsbHostDevice connected: " + device);
        }

        if (device == null) {
            Log.d(UsbHostDevice.LOG_TAG, "No devices found");
            return null;
        }

        openDevice(device);

        if (!usbManager.hasPermission(device)) {
            usbManager.requestPermission(device, intent);
        }

        if (!usbManager.hasPermission(device)) {
            Log.d(UsbHostDevice.LOG_TAG, "Permission denied for accessory " + device);
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
                    1000000,
                    0
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
            UsbInterface intf = device.getInterface(0);
            endpoint = intf.getEndpoint(0);
            connection = usbManager.openDevice(device);
            connection.claimInterface(intf, true);
            dataWithEvent.getEvent().startEvent();
        }
    }

    @Override
    public void stop() {
        if (dataWithEvent != null && dataWithEvent.getEvent() != null) {
            try {
                dataWithEvent.getEvent().stopEvent();
            } catch (InterruptedException e) {
                Log.d(UsbHostDevice.LOG_TAG, "interrupted exception: " + e.toString());
            }
        }
        closeDevice();
        context.unregisterReceiver(usbBroadcastReceiver);
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) { this.setting = setting; }
}