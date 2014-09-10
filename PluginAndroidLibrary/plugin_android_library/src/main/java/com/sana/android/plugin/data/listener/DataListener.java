package com.sana.android.plugin.data.listener;

import java.util.concurrent.TimeUnit;

/**
 * @author Han Lin
 */
public interface DataListener {
    public void startListening();
    public void stopListening();
    public void stopListening(long timeout, TimeUnit unit);
}
