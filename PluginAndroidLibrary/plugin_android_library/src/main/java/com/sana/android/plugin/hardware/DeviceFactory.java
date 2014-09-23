package com.sana.android.plugin.hardware;

import android.util.Log;

import com.sana.android.plugin.communication.MimeType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by hanlin on 9/14/14.
 */
public class DeviceFactory {
    private static final String LOG_TAG = "DeviceFactory";
    private static final String CLASS_NOT_FOUND_EXCEPTION_FORMAT =
            "Class %s is not found. The implementation might be erroneous.";
    private static final String NO_SUCH_METHOD_FORMAT =
            "Nullable constructor is not found in class - %s";
    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Unexpected fatal error happened.";

    public static GeneralDevice getDeviceInstance(
            Feature feature, CaptureSetting setting) {
        Class deviceClass = null;
        try {
            deviceClass = Class.forName(feature.getDeviceClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, String.format(CLASS_NOT_FOUND_EXCEPTION_FORMAT, feature.getDeviceClassName()));
        }
        Constructor deviceConstructor = null;
        try {
            deviceConstructor = deviceClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, String.format(NO_SUCH_METHOD_FORMAT, feature.getDeviceClassName()));
        }
        GeneralDevice instance = null;
        try {
            instance = (GeneralDevice) deviceConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, UNEXPECTED_ERROR_MESSAGE);
        }
        instance.setCaptureSetting(setting);
        return instance;
    }
}
