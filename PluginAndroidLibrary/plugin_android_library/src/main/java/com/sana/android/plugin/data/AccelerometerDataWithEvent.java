package com.sana.android.plugin.data;

import com.sana.android.plugin.data.event.AccelerometerDataEvent;
import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.listener.DataListener;
import com.sana.android.plugin.data.listener.TimedListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by mashiro on 10/3/2014.
 */
public class AccelerometerDataWithEvent implements DataWithEvent {
    private class InternalEventListener extends TimedListener {
        public InternalEventListener() {
        }

        public InternalEventListener(
                Object sender,
                long interval,
                TimeUnit intervalUnit
        ) {
            super(sender, interval, intervalUnit);
        }
        @Override
        public void processData(Object sender, Object[] data) {
            if (data.length > 0 &&
                    data instanceof AccelerometerDataEvent.AccelerometerData[]) {
                recordedData.offer(
                        (AccelerometerDataEvent.AccelerometerData) data[data.length - 1]);
            }
        }
    }
    private LinkedBlockingQueue<AccelerometerDataEvent.AccelerometerData> recordedData;
    private AccelerometerDataEvent event;
    private InternalEventListener listener;

    /**
     *
     * @param sender
     * @param updateInterval        The update interval of the accelerometer event.
     *                                 When the listener's processData is called
     *                                 multiple pieces of data may be passed in as the
     *                                 argument. However only the last one will be saved
     *                                 in this DataWithEvent instance.
     * @param updateIntervalUnit
     */
    public AccelerometerDataWithEvent(
            Object sender,
            long updateInterval,
            TimeUnit updateIntervalUnit
    ) {
        recordedData =
                new LinkedBlockingQueue<AccelerometerDataEvent.AccelerometerData>();
        event = new AccelerometerDataEvent(sender);
        listener = new InternalEventListener(sender, updateInterval, updateIntervalUnit);
        listener.startListening();
    }

    @Override
    public BaseDataEvent getEvent() {
        return event;
    }

    @Override
    public void dispose() throws InterruptedException {
        listener.stopListening();
        event.stopEvent();
    }
}
