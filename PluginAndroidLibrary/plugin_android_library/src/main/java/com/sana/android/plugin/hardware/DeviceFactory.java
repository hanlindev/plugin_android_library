package com.sana.android.plugin.hardware;

import android.util.Log;
import android.content.Context;

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
            "Nullary constructor is not found in class - %s";
    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Unexpected fatal error happened.";

    /**
     * Attempt to create the device class instance of the provided feature.
     * If such class is not implemented, return null.
     *
     * @param feature    The feature for which the device class is created.
     * @param setting    The setting to be supplied to the device instance.
     * @return The device class instance or null if it is not implemented.
     */
    public static GeneralDevice getDeviceInstance(
            Feature feature, CaptureSetting setting) {
        if (!feature.isDeviceClassImplemented()) {
            return null;
        }

        Class deviceClass = feature.getDeviceClass();
        Constructor deviceConstructor = null;
        try {
            deviceConstructor = deviceClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e(
                    LOG_TAG,
                    String.format(
                            NO_SUCH_METHOD_FORMAT,
                            deviceClass.getName()
                    )
            );
        }
        GeneralDevice instance = null;
        try {
            instance = (GeneralDevice) deviceConstructor.newInstance();
        } catch (Exception e) {
            if (e instanceof InstantiationException ||
                    e instanceof IllegalAccessException ||
                    e instanceof InvocationTargetException) {
                e.printStackTrace();
                Log.e(LOG_TAG, UNEXPECTED_ERROR_MESSAGE);
            }
        }
        if (instance != null) {
            instance.setCaptureSetting(setting);
        }
        return instance;
    }
}
