package com.sana.android.plugin.data.listener;

import java.util.concurrent.TimeUnit;

/**
 * @author Han Lin
 */
public interface DataListener {
    public void startListening();
    public void stopListening();
    public Object getExpectedSender();
    public void stopListening(long timeout, TimeUnit unit);
    public void putData(Object[] data);
    public void processData(Object sender, Object[] data);
}
