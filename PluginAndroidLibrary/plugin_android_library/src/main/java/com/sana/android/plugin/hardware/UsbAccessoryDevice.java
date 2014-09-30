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

    UsbAccessory accessory;
    ParcelFileDescriptor accessoryFileDescriptor;
    FileInputStream accessoryInput;
    FileOutputStream accessoryOutput;
    CaptureSetting setting;
    BinaryDataWithPollingEvent dataWithEvent;

    public UsbAccessoryDevice(Context context) {
        super(context);
    }

    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            Log.d("good, ", "very good");

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            openAccessory(accessory);
                        }
                    }
                    else {
                        Log.d("debuggg: ", "permission denied for accessory " + accessory);
                    }
                }
            }
        }
    };

    private void openAccessory(UsbAccessory accessory)
    {
        accessoryFileDescriptor = usbManager.openAccessory(accessory);
        if (accessoryFileDescriptor != null)
        {
            this.accessory = accessory;
            FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
            accessoryInput = new FileInputStream(fd);
            accessoryOutput = new FileOutputStream(fd);

            Log.d("accessory opened","");
        }
        else
        {
            Log.d("accessory open fail","");
        }
    }

    private void closeAccessory()
    {
        // TODO: disable USB operations in the app
        try
        {
            if (accessoryInput != null) {
                accessoryInput.close();
                Log.d("closed accessory input stream", "");
            }
        }
        catch (IOException e)
        {
            Log.d("exception closing", e.toString());
        }
        finally {
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
            Toast.makeText(context, "null accessory", Toast.LENGTH_LONG).show();
            return null;
        }

        openAccessory(accessory);

        if (accessoryInput == null) {
            return null;
        }

        if (!usbManager.hasPermission(accessory)) {
            return null;
        }

        Toast.makeText(context, accessoryInput.toString(), Toast.LENGTH_LONG).show();

        try {
            dataWithEvent = new BinaryDataWithPollingEvent(
                    Feature.USB_ACCESSORY,
                    MimeType.TEXT_PLAIN,
                    null,
                    this,
                    accessoryInput,
                    8
            );
        } catch (FileNotFoundException e) {
            Log.d("file not found", e.toString());
        } catch (URISyntaxException e) {
            Log.d("uri exception", e.toString());
        }

        return dataWithEvent;

        /*thread = new Thread(null, this, "ASDF");
        thread.start();

        return null;*/
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
                Log.d("stopped event", "");
            } catch (InterruptedException e) {
                Log.d("Interrupt exception", e.toString());
            }
        }
        closeAccessory();
        context.unregisterReceiver(usbBroadcastReceiver);
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) {
        this.setting = setting;
    }

    /*@Override
    public void run() {
        byteStream = new byte[80];
        int num = 0;
        String message = "";
        while (num >= 0) {
            try {
                num = accessoryInput.read(byteStream);
                accessoryInput.close();
                Log.d("num = ", num+"");
            } catch (IOException e) {
                Log.d("Exception in USB accessory input reading", e.toString());
            }
            for (int i = 0; i < byteStream.length; i++)
                message += byteStream[i];
            Log.d("message = ", message);
        }
        Log.d("thread stopped","");
    }*/
}
