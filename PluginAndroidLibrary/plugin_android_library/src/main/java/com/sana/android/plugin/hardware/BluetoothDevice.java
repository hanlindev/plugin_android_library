package com.sana.android.plugin.hardware;

import android.content.ContentResolver;
import android.os.Environment;

import com.sana.android.plugin.application.CommManager;
import com.sana.android.plugin.data.BinaryDataWithPollingEvent;
import com.sana.android.plugin.data.DataWithEvent;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
/**
 * Created by hanlin on 9/14/14.
 */
public class BluetoothDevice extends AudioRecordDevice implements GeneralDevice {
    private ContentResolver contentResolver;
    @Override
    //prepare is to store location of recorded audio?
    public DataWithEvent prepare() {
        //CommManager cm = CommManager.getInstance();
        //File initialFile = new File(super.mFileName);
        //InputStream is = new FileInputStream(initialFile);
        //BinaryDataWithPollingEvent pollingData = new BinaryDataWithPollingEvent(Feature.BLUETOOTH, CommManager.getInstance().getMimeType(), cm.getUri(), this, int uri_size);
        return null;
    }
}
