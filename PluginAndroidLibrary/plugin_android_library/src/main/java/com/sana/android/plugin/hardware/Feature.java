package com.sana.android.plugin.hardware;

import com.sana.android.plugin.errors.UnsupportedDeviceError;

/**
 * Created by Chen Xi on 9/2/2014.
 * A subset of features from PackageManager. We don't include everything from PM because
 * it is not intended to support all sorts of connections. These are the things in our plan.
 *
 * @see android.content.pm.PackageManager
 */
public enum Feature {
    BLUETOOTH("android.hardware.bluetooth", "bluetooth"),
    BLUETOOTH_LE("android.hardware.bluetooth_le", "bluetoothLe"),
    CAMERA_REAR("android.hardware.camera", "rearCamera"),
    CAMERA_FRONT("android.hardware.camera.front", "frontCamera"),
    CONSUMER_IR("android.hardware.consumerir", "consumerIr"),
    USB_ACCESSORY("android.hardware.usb.accessory", "usbAccessory"),
    WIFI_DIRECT("android.hardware.wifi.direct", "wifiDirect"),
    MICROPHONE("android.hardware.microphone", "microphone"),
    ACCELEROMETER("android.hardware.sensor.accelerometer", "accelerometer");

    private static final String BLUETOOTH_CLASS_NAME = "BluetoothDevice";
    private static final String BUILTIN_CLASS_NAME = "BuiltinDevice";
    private static final String USB_CLASS_NAME = "UsbDevice";

    private String featureName;
    private String commonName;
    private Feature(String featureName, String commonName) {
        this.featureName = featureName;
        this.commonName = commonName;
    }

    public String toString() {
        return this.featureName;
    }

    public String toCommonName() {
        return this.commonName;
    }

    public static Feature getFromCommonName(String commonName) {
        if (commonName != null) {
            for (Feature feature : Feature.values()) {
                if (feature.commonName.equals(commonName)) {
                    return feature;
                }
            }
        }
        return null;
    }

    public String getDeviceClassName() {
        String result = null;
        switch (this) {
            case BLUETOOTH:
                result = BLUETOOTH_CLASS_NAME;
                break;
            case CAMERA_REAR:
            case CAMERA_FRONT:
            case MICROPHONE:
                result = BUILTIN_CLASS_NAME;
                break;
            case USB_ACCESSORY:
                result = USB_CLASS_NAME;
                break;
            default:
                throw new UnsupportedDeviceError(this);
        }
        return result;
    }
}
