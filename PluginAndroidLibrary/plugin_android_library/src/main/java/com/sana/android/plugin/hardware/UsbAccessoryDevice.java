package com.sana.android.plugin.hardware;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by quang on 9/16/14.
 */
public class UsbAccessoryDevice extends UsbGeneralDevice {

    private static final String LOG_TAG = "UsbAccessoryDevice";
    private static final int BUFFER_DEFAULT_SIZE = 8;

    private UsbAccessory accessory;
    private ParcelFileDescriptor accessoryFileDescriptor;
    private FileInputStream accessoryInput;
    private FileOutputStream accessoryOutput;
    private CaptureSetting setting;
    private BinaryDataWithPollingEvent dataWithEvent;
    private int bufferSize;

    public UsbAccessoryDevice() {}

    public UsbAccessoryDevice(Context context) {
        super(context);
        this.bufferSize = BUFFER_DEFAULT_SIZE;
    }

    public UsbAccessoryDevice(Context context, int bufferSize) {
        super(context);
        this.bufferSize = bufferSize;
    }

    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (accessory != null) {
                            openAccessory(accessory);
                        }
                    } else {
                        Log.d(UsbAccessoryDevice.LOG_TAG, "Permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };

    private void openAccessory(UsbAccessory accessory) {
        accessoryFileDescriptor = usbManager.openAccessory(accessory);
        if (accessoryFileDescriptor != null) {
            this.accessory = accessory;
            FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
            accessoryInput = new FileInputStream(fd);
            accessoryOutput = new FileOutputStream(fd);

            Log.d(UsbAccessoryDevice.LOG_TAG, "Accessory open: SUCCESS");
        } else {
            Log.d(UsbAccessoryDevice.LOG_TAG, "Accessory open: FAILURE");
        }
    }

    private void closeAccessory() {
        try {
            if (accessoryInput != null) {
                accessoryInput.close();
                Log.d(UsbAccessoryDevice.LOG_TAG, "Closed accessory input stream");
            }
        } catch (IOException e) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "Exception closing accessory: " + e.toString());
        } finally {
            accessoryFileDescriptor = null;
            accessory = null;
        }
    }

    @Override
    public DataWithEvent prepare() {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter accessoryFilter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbBroadcastReceiver, accessoryFilter);

        final UsbAccessory[] accessoryList = usbManager.getAccessoryList();
        if (accessoryList != null && accessoryList.length >= 1) {
            accessory = accessoryList[0];
        }

        if (accessory == null) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "No accessories found");
            return null;
        }

        openAccessory(accessory);

        if (accessoryInput == null) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "Input stream cannot be opened");
            return null;
        }

        if (!usbManager.hasPermission(accessory)) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "Permission denied for accessory " + accessory);
            return null;
        }

        try {
            dataWithEvent = new BinaryDataWithPollingEvent(
                    Feature.USB_ACCESSORY,
                    MimeType.TEXT_PLAIN,
                    null,
                    this,
                    accessoryInput,
                    bufferSize
            );
        } catch (FileNotFoundException e) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "file not found: " + e.toString());
        } catch (URISyntaxException e) {
            Log.d(UsbAccessoryDevice.LOG_TAG, "uri exception: " + e.toString());
        }

        return dataWithEvent;
    }

    @Override
    public void begin() {
        if (dataWithEvent != null) {
            dataWithEvent.getEvent().startEvent();
        }
    }

    @Override
    public void stop() {
        if (dataWithEvent != null && dataWithEvent.getEvent() != null) {
            try {
                dataWithEvent.getEvent().stopEvent();
            } catch (InterruptedException e) {
                Log.d(UsbAccessoryDevice.LOG_TAG, "interrupted exception: " + e.toString());
            }
        }
        closeAccessory();
        context.unregisterReceiver(usbBroadcastReceiver);
    }

    public void reset() {
        if (dataWithEvent != null) {
            try {
                dataWithEvent.dispose();
            } catch (InterruptedException e) {
                Log.e(
                        UsbAccessoryDevice.LOG_TAG,
                        "Operation interrupted while disposing the previous data.",
                        e
                );
            }
        }
        dataWithEvent = null;
    }

    public void setCaptureSetting(CaptureSetting setting) {
        this.setting = setting;
    }
}
