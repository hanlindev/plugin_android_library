package com.sana.android.plugin.tests.hardware;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.errors.InvalidCaptureSettingsError;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.DeviceFactory;
import com.sana.android.plugin.hardware.Feature;
import com.sana.android.plugin.hardware.GeneralDevice;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by DIdiHL on 10/7/2014.
 */
public class DeviceFactoryTests extends InstrumentationTestCase {
    public void testGetDeviceInstance() {
        for (Feature feature : Feature.values()) {
            testGetDeviceInstanceForFeature(feature);
        }
    }

    private void testGetDeviceInstanceForFeature(Feature feature) {
        if (feature.isDeviceClassImplemented()) {
            testGetDeviceInstanceForImplementedFeature(feature);
        } else {
            testGetDeviceInstanceForUnimplementedFeature(feature);
        }
    }

    private void testGetDeviceInstanceForImplementedFeature(Feature feature) {

        GeneralDevice newDevice = null;

        try {
            newDevice =
                    DeviceFactory.getDeviceInstance(feature, CaptureSetting.defaultSetting(feature, MimeType.AUDIO));
        } catch (InvalidCaptureSettingsError e) {
            if (feature != Feature.ACCELEROMETER) {
                throw e;
            }
        }

        if (newDevice != null) {
            assertEquals(
                    "Generated device should be the same as predefined class.",
                    newDevice.getClass().getName(),
                    feature.getDeviceClass().getName()
            );
        }
    }

    private void testGetDeviceInstanceForUnimplementedFeature(Feature feature) {
        GeneralDevice newDevice =
                DeviceFactory.getDeviceInstance(feature, CaptureSetting.defaultSetting(feature, MimeType.VIDEO));
        assertNull(
                "Null should be returned for unimplemented feature.",
                newDevice
        );
    }
}
