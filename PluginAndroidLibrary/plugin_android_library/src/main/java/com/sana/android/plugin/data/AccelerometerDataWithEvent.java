package com.sana.android.plugin.data;

import com.sana.android.plugin.data.event.BaseDataEvent;
import com.sana.android.plugin.data.listener.DataListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by mashiro on 10/3/2014.
 */
public class AccelerometerDataWithEvent implements DataListener, DataWithEvent {
    @Override
    public void startListening() {

    }

    @Override
    public void stopListening() {

    }

    @Override
    public void setExpectedSender(Object sender) {

    }

    @Override
    public Object getExpectedSender() {
        return null;
    }

    @Override
    public void stopListening(long timeout, TimeUnit unit) {

    }

    @Override
    public void putData(Object[] data) {

    }

    @Override
    public void processData(Object sender, Object[] data) {

    }

    @Override
    public BaseDataEvent getEvent() {
        return null;
    }
}
