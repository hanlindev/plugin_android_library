package com.sana.android.plugin.hardware;

import com.sana.android.plugin.data.DataWithEvent;

/**
 * Created by hanlin on 9/13/14.
 */
public interface GeneralDevice {
    /**
     * Call this method to prepare the sensing device for recording.
     * Here, it is not enforced here that prepare must precede begin,
     * so the subclass can either allow or disallow that.
     *
     * @return The data instance that has the appropriate event type.
     */
    public DataWithEvent prepare();
    public void begin();
    public void stop();
    public void reset();
}
