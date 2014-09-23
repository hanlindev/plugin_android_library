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

import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by quang on 9/16/14.
 */
public class UsbAccessoryDevice extends UsbGeneralDevice { //implements Runnable {

    UsbAccessory accessory;
    ParcelFileDescriptor accessoryFileDescriptor;
    FileInputStream accessoryInput;
    FileOutputStream accessoryOutput;
    CaptureSetting setting;
    BinaryDataWithPollingEvent event;

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
        Log.d("openning", accessory.toString());
        accessoryFileDescriptor = usbManager.openAccessory(accessory);
        if (accessoryFileDescriptor != null)
        {
            this.accessory = accessory;
            FileDescriptor fd = accessoryFileDescriptor.getFileDescriptor();
            accessoryInput = new FileInputStream(fd);
            accessoryOutput = new FileOutputStream(fd);

            Log.d("accessory opened","");
            // TODO: enable USB operations in the app
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
            if (accessoryFileDescriptor != null) {
                accessoryFileDescriptor.close();
                Log.d("closed accessory file descriptor", "");
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
        else openAccessory(accessory);

        if (accessoryInput == null) {
            Toast.makeText(context, "null input", Toast.LENGTH_LONG).show();
            return null;
        }
        if (!usbManager.hasPermission(accessory)) {
            Toast.makeText(context, "no permission", Toast.LENGTH_LONG).show();
            return null;
        }

        Toast.makeText(context, accessoryInput.toString(), Toast.LENGTH_LONG).show();

        try {
            event = new BinaryDataWithPollingEvent(
                    null, // Feature.USB_ACCESSORY
                    null, // MimeType.BINARY ?
                    null,
                    this,
                    accessoryInput,
                    100000
            );
        } catch (FileNotFoundException e) {
            Log.d("file not found", e.toString());
        } catch (URISyntaxException e) {
            Log.d("uri exception", e.toString());
        }
        return event;
    }

    @Override
    public void begin() {
        if (accessory != null) {
            openAccessory(accessory);
        }
    }

    @Override
    public void stop() {
        if (accessory != null) {
            closeAccessory();
            context.unregisterReceiver(usbBroadcastReceiver);
        }
    }

    public void reset() {

    }

    public void setCaptureSetting(CaptureSetting setting) {
        this.setting = setting;
    }

/*    @Override
    public void run() {
        byteStream = new byte[MAX_BYTE_ARRAY_LENGTH];

        try {
            accessoryInput.read(byteStream);
        } catch (IOException e) {
            Log.d("Exception in USB accessory input reading", e.toString());
        }
    }*/
}
