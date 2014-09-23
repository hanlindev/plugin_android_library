package com.sana.android.plugin.hardware;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;

import com.sana.android.plugin.data.DataWithEvent;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by quang on 9/16/14.
 */
public class UsbHostDevice extends UsbGeneralDevice implements Runnable {

    UsbDevice device;
    Thread thread;

    @Override
    public DataWithEvent prepare() {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter accessoryFilter = new IntentFilter(ACTION_USB_PERMISSION);
        //context.registerReceiver(usbBroadcastReceiver, accessoryFilter);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        if (deviceIterator.hasNext())
            device = deviceIterator.next();
        else
            device = null;

        return null;
    }

    @Override
    public void begin() {
        if (device != null) {
            thread = new Thread();
            thread.start();
        }
    }

    @Override
    public void stop() {
        if (device != null) {
            thread.stop();
        }
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) {

    }

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
    }
}
