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
    BLUETOOTH("android.hardware.bluetooth", "bluetooth", BluetoothDevice.class),
    BLUETOOTH_LE(
            "android.hardware.bluetooth_le",
            "bluetoothLe",
            BluetoothDevice.class
    ),
    CAMERA_REAR(
            "android.hardware.camera",
            "rearCamera",
            BuiltinCameraDevice.class
    ),
    CAMERA_FRONT(
            "android.hardware.camera.front",
            "frontCamera",
            BuiltinCameraDevice.class
    ),
    CONSUMER_IR("android.hardware.consumerir", "consumerIr"),
    USB_ACCESSORY(
            "android.hardware.usb.accessory",
            "usbAccessory",
            UsbAccessoryDevice.class
    ),
    USB_HOST(
            "android.hardware.usb.host",
            "usbHost",
            UsbHostDevice.class
    ),
    WIFI_DIRECT("android.hardware.wifi.direct", "wifiDirect"),
    MICROPHONE(
            "android.hardware.microphone",
            "microphone",
            BuiltinAudioDevice.class
    ),
    ACCELEROMETER("android.hardware.sensor.accelerometer", "accelerometer");

    private String featureName;
    private String commonName;
    private Class deviceClass;

    private Feature(String featureName, String commonName) {
        this(featureName, commonName, null);
    }
    private Feature(String featureName, String commonName, Class deviceClass) {
        this.featureName = featureName;
        this.commonName = commonName;
        this.deviceClass = deviceClass;
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

    public Class getDeviceClass() {
        if (!this.isDeviceClassImplemented()){
            throw new UnsupportedDeviceError(this);
        } else {
            return this.deviceClass;
        }
    }

    public boolean isDeviceClassImplemented() {
        return this.deviceClass != null;
    }
}
