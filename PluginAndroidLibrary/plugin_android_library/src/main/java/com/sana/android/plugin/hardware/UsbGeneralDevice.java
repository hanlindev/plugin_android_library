package com.sana.android.plugin.hardware;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.sana.android.plugin.data.DataWithEvent;

import java.io.IOException;

/**
 * Created by hanlin on 9/14/14.
 */
public abstract class UsbGeneralDevice implements GeneralDevice {

    Context context;
    UsbManager usbManager;
    byte[] byteStream;

    final int MAX_BYTE_ARRAY_LENGTH = 1000000;
    final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public UsbGeneralDevice() {}
    public UsbGeneralDevice(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }
}
