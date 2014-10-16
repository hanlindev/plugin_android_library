package com.sana.android.plugin.data.event;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.sana.android.plugin.errors.InvalidArgumentError;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by mashiro on 10/3/2014.
 */
public class AccelerometerDataEvent extends BaseDataEvent implements SensorEventListener, Runnable {
    public static class AccelerometerData {
        private static final int INDEX_X = 0;
        private static final int INDEX_Y = 1;
        private static final int INDEX_Z = 2;
        private static final String INVALID_ARGUMENT_ERROR_FORMAT =
                "Expecting SensorType from a sensor with a type of %d. " +
                        "Received one with the type of %d.";

        private float x;
        private float y;
        private float z;

        public AccelerometerData(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                throw new InvalidArgumentError(
                        String.format(
                                INVALID_ARGUMENT_ERROR_FORMAT,
                                Sensor.TYPE_ACCELEROMETER,
                                event.sensor.getType()
                        )
                );
            }
            this.x = event.values[INDEX_X];
            this.y = event.values[INDEX_Y];
            this.z = event.values[INDEX_Z];
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        public float getZ() {
            return this.z;
        }
    }

    private static final long SHUTDOWN_TIMEOUT = 5;
    private static final TimeUnit SHUTDOWN_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private ExecutorService notificationMasterThread;
    private LinkedBlockingQueue<AccelerometerData> queuedData;

    public AccelerometerDataEvent(Object sender) {
        super(sender);
        this.notificationMasterThread = Executors.newSingleThreadExecutor();
        queuedData = new LinkedBlockingQueue<AccelerometerData>();
    }

    @Override
    public void startEvent() {
        this.notificationMasterThread.submit(this);
    }

    @Override
    public void stopEvent() throws InterruptedException {
        this.notificationMasterThread.shutdown();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.queuedData.offer(new AccelerometerData(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void run() {
        while (true) {
            AccelerometerData newData = this.queuedData.poll();
            this.notifyListeners(new AccelerometerData[] {newData});
        }
    }
}
