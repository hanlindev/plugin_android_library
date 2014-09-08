package com.sana.android.plugin.hardware;

import com.sana.android.plugin.application.DataConverter;

/**
 * Created by MoeMoeMashiro on 9/9/2014.
 */
public class BuiltinDevice<T> extends DeviceBase<T> {

    @Override
    public void prepare() {

    }

    @Override
    public void start() {

    }

    @Override
    public <T1> T1 stop(DataConverter<T1> converter) {
        return null;
    }
}
