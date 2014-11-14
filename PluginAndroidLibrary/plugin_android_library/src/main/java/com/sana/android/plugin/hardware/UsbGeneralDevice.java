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

    protected Context context;
    protected UsbManager usbManager;

    final String ACTION_USB_PERMISSION = "com.sana.android.plugin.hardware.USB_PERMISSION";

    public UsbGeneralDevice() {}
    public UsbGeneralDevice(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }
}
