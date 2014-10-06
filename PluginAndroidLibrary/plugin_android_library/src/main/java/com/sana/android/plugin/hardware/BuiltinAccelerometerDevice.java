package com.sana.android.plugin.hardware;

import com.sana.android.plugin.data.AccelerometerDataWithEvent;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.event.AccelerometerDataEvent;

/**
 * Created by mashiro on 10/3/2014.
 */
public class BuiltinAccelerometerDevice implements GeneralDevice {
    private AccelerometerDataWithEvent savedData;
    @Override
    public DataWithEvent prepare() {
//        savedData = new AccelerometerDataWithEvent(this);
        return null;
    }

    @Override
    public void begin() {
        this.savedData.getEvent().startEvent();
    }

    @Override
    public void stop() {
        try {
            this.savedData.getEvent().stopEvent();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void setCaptureSetting(CaptureSetting setting) {

    }
}
