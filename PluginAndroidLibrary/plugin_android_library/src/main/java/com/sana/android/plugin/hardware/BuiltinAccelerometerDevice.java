package com.sana.android.plugin.hardware;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.sana.android.plugin.data.AccelerometerDataWithEvent;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.event.AccelerometerDataEvent;
import com.sana.android.plugin.errors.InvalidCaptureSettingsError;

import java.util.concurrent.TimeUnit;

/**
 * Created by mashiro on 10/3/2014.
 */
public class BuiltinAccelerometerDevice implements GeneralDevice {
    private static final String LOG_TAG = "BuiltinAccelerometerDevice";
    private static final long UPDATE_INTERVAL = 1;
    private static final TimeUnit UPDATE_INTERVAL_TIME_UNIT = TimeUnit.SECONDS;
    private CaptureSetting setting;
    private Sensor senAccelerometer;

    private AccelerometerDataWithEvent savedData;
    @Override
    public DataWithEvent prepare() {
        savedData = new AccelerometerDataWithEvent(
                this, UPDATE_INTERVAL, UPDATE_INTERVAL_TIME_UNIT);

        setting.getSensorManager().registerListener(
                (AccelerometerDataEvent) savedData.getEvent(),
                senAccelerometer ,
                SensorManager.SENSOR_DELAY_NORMAL
        );
        return savedData;
    }

    @Override
    public void begin() {
        this.savedData.getEvent().startEvent();
    }

    @Override
    public void stop() {
        try {
            this.savedData.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        if (savedData != null) {
            try {
                savedData.dispose();
            } catch (InterruptedException e) {
                Log.e(
                        LOG_TAG,
                        "Operation interrupted while disposing the previous data.",
                        e
                );
            }
        }
        savedData = null;
    }

    @Override
    public void setCaptureSetting(CaptureSetting setting) {
        validateCaptureSetting(setting);
        this.setting = setting;
        senAccelerometer = setting.getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void validateCaptureSetting(CaptureSetting setting) {
        if (setting.getSensorManager() == null) {
            throw new InvalidCaptureSettingsError("Required attribute sensorManager is missing.");
        }
    }
}
