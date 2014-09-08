package com.sana.android.plugin.application;

/**
 * Created by Han Lin on 9/8/2014.
 */
public interface DataConverter<T> {
    public T convert(Byte[] bytes);
}
