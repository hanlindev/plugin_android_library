package com.sana.android.plugin.hardware;

/**
 * Created by Chen Xi on 9/2/2014.
 * A subset of features from PackageManager. We don't include everything from PM because
 * it is not intended to support all sorts of connections. These are the things in our plan.
 *
 * @see android.content.pm.PackageManager
 */
public enum Feature {
    BLUETOOTH("android.hardware.bluetooth", "bluetooth", "BluetoothDevice"),
    BLUETOOTH_LE("android.hardware.bluetooth_le", "bluetoothLe", "BluetoothDevice"),
    CAMERA_REAR("android.hardware.camera", "rearCamera", "BuiltinDevice"),
    CAMERA_FRONT("android.hardware.camera.front", "frontCamera", "BuiltinDevice"),
    CONSUMER_IR("android.hardware.consumerir", "consumerIr", "InfraredDevice"),
    USB_ACCESSORY("android.hardware.usb.accessory", "usbAccessory", "UsbDevice"),
    WIFI_DIRECT("android.hardware.wifi.direct", "wifiDirect", "WifiDevice"),
    MICROPHONE("android.hardware.microphone", "microphone", "BuiltinDevice"),
    ACCELEROMETER("android.hardware.sensor.accelerometer", "accelerometer", "BuiltinDevice");

    private String featureName;
    private String commonName;
    private String deviceClassName;
    private Feature(String featureName, String commonName, String deviceClassName) {
        this.featureName = featureName;
        this.commonName = commonName;
        this.deviceClassName = deviceClassName;
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public String getDeviceClassName() {
        return this.deviceClassName;
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
}
