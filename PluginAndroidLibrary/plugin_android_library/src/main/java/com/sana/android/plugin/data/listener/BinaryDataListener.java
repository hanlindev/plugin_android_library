package com.sana.android.plugin.data.listener;

/**
 * Created by hanlin on 9/10/14.
 */
public interface BinaryDataListener extends DataListener {
    public void putBytes(Byte[] bytes);
    public void updateBytes(Byte[] bytes);
}
