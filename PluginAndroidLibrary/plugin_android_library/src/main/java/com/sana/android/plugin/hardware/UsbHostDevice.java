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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by quang on 9/16/14.
 */
public class UsbHostDevice extends UsbGeneralDevice {//implements Runnable {

    private static final String LOG_TAG = "UsbHostDevice";

    UsbDevice device;
    ParcelFileDescriptor deviceFileDescriptor;
    FileInputStream deviceInput;
    FileOutputStream deviceOutput;
    UsbDeviceConnection connection;
    UsbInterface usbInterface;
    UsbEndpoint endpoint;

    BinaryDataWithPollingEvent dataWithEvent;

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
                        if(device != null){
                            openDevice(device);
                        }
                    }
                    else {
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
        deviceInput = null;
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

        if (deviceInput == null) {
            Log.d(UsbHostDevice.LOG_TAG, "Input stream cannot be opened");
            return null;
        }

        if (!usbManager.hasPermission(device)) {
            Log.d(UsbHostDevice.LOG_TAG, "Permission denied for accessory " + device);
            return null;
        }

        try {
            dataWithEvent = new BinaryDataWithPollingEvent(
                    Feature.USB_HOST,
                    MimeType.TEXT_PLAIN,
                    null,
                    this,
                    deviceInput,
                    8
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
//            thread = new Thread();
//            thread.start();
        }
    }

    @Override
    public void stop() {
        if (device != null) {
        }
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) {

    }
/*
    @Override
    public void run() {
        byte[] bytes = new byte[16384];
        int TIMEOUT = 0;
        boolean forceClaim = true;

        UsbInterface intf = device.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);
        UsbDeviceConnection connection = usbManager.openDevice(device);
        connection.claimInterface(intf, forceClaim);
        connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
    }*/
}
