package com.sana.android.plugin.application;

import android.content.ContentResolver;
import android.util.Log;

import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.data.DataWithEvent;
import com.sana.android.plugin.data.listener.DataListener;
import com.sana.android.plugin.errors.InvalidInvocationError;
import com.sana.android.plugin.hardware.CaptureSetting;
import com.sana.android.plugin.hardware.DeviceFactory;
import com.sana.android.plugin.hardware.Feature;
import com.sana.android.plugin.hardware.GeneralDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.Vector;

/**
 * The main helper class that facilitates data capturing from various sensing
 * devices. You should use this class in most cases. Sample code snippets:
 * <ol>
 *     <li>
 *         Create an instance variable:
 *         <pre>
 *             <code>
 *                 private CaptureManager captureManager;
 *             </code>
 *         </pre>
 *     </li>
 *     <li>
 *         Optional: Implement your own data event listener. You can extend from existing listeners
 *         including {@link com.sana.android.plugin.data.listener.DataChunkListener} and
 *         {@link com.sana.android.plugin.data.listener.TimedListener}. If these classes don't
 *         meet your requirement, you can implement {@link com.sana.android.plugin.data.listener.DataListener}.
 *     </li>
 *     <li>
 *         Instantiate the captureManager instance variable in onCreate method.
 *         <pre>
 *             <code>
 *                 protected void onCreate(Bundle savedInstanceState) {
 *                     <b>//...</b>
 *                     captureManager = new CaptureManager(Feature.BLUETOOTH, MimeType.Audio, getContentResolver());
 *                     captureManager.addListener(new MyListener());// You have to call addListener before prepare
 *                     captureManager.prepare();
 *                     <b>//...</b>
 *                 }
 *             </code>
 *         </pre>
 *         Additionally, if you need to pass additional or required capture setting, you can do this:
 *         <pre>
 *             <code>
 *                 protected void onCreate(Bundle savedInstanceState) {
 *                     <b>//...</b>
 *                     CaptureSetting captureSetting = new CaptureSetting();
 *                     // Do something with captureSetting
 *                     captureManager = new CaptureManager(Feature.BLUETOOTH, MimeType.Audio, getContentResolver(), captureSetting);
 *                     captureManager.addListener(new MyListener());// You have to call addListener before prepare
 *                     captureManager.prepare();
 *                     <b>//...</b>
 *                 }
 *             </code>
 *         </pre>
 *     </li>
 *     <li>
 *         Call {@link #begin()} and {@link #stop()} at appropriate places. Here they are called in a button
 *         click listener.
 *         <pre>
 *             <code>
 *                 public void recordButtonListener(View view) {
 *                     if (recording) {
 *                         captureManager.stop();
 *                         captureManager.reset();// You have to call reset before calling prepare again.
 *                         recording = false;
 *                     } else {
 *                         captureManager.begin();
 *                         recording = true;
 *                     }
 *                 }
 *             </code>
 *         </pre>
 *     </li>
 *     <li>
 *         When data is ready to be sent to Sana, probably after some post processing, call
 *         {@link com.sana.android.plugin.application.CommManager#sendData(android.app.Activity)}.
 *         <pre>
 *             <code>
 *                 CommManager.getInstance().sendData(this);
 *             </code>
 *         </pre>
 *     </li>
 * </ol>
 *
 * @author Han Lin
 */
public class CaptureManager {
    private static final String LOG_TAG = "CaptureManager";
    private static final String RESET_NOT_CALLED_ERROR_MSG =
            "reset must be called before prepare";
    private static final String PREPARE_NOT_CALLED_ERROR_MSG =
            "prepare must be called before begin";
    private static final String STOP_INTERRUPTED_EXCEPTION_MSG =
            "Call to method - stop is interrupted.";

    private GeneralDevice dataSource;
    private DataWithEvent data;
    private Vector<DataListener> listeners;
    public CaptureManager(
            Feature source, MimeType type, ContentResolver contentResolver) {
        this(
                source,
                type,
                contentResolver,
                CaptureSetting.defaultSetting(source, type)
        );
    }

    /**
     * Create a CaptureManager that receives data of the specified MIME type and
     * from the given Feature. The
     *
     * @param source    The device feature from which data will come from.
     * @param type      The MIME type of the data intended to be captured.
     * @param setting   The setting to be passed to the sensor.
     */
    public CaptureManager(
            Feature source,
            MimeType type,
            ContentResolver contentResolver,
            CaptureSetting setting
    ) {
        if (setting == null) {
            setting = CaptureSetting.defaultSetting(source, type);
        }
        setting.setContentResolver(contentResolver);
        this.dataSource =
                DeviceFactory.getDeviceInstance(source, setting);
        this.listeners = new Vector<DataListener>();
    }



    /**
     * Add a listener to the sensing device. Call this method if you wish
     * to receive updates from the device. You can extend the existing
     * DataChunkListener or TimedListener. The listening configuration can
     * be modified in those classes.
     *
     * @param listener    The listener instance that is intended to receive
     *                    updates.
     */
    public void addListener(DataListener listener) {
        listener.setExpectedSender(this.dataSource);
        this.listeners.add(listener);
    }

    /**
     * Remove the listener. A listener may be added multiple times. This
     * method will remove all of them.
     *
     * @param listener
     */
    public void removeListener(DataListener listener) {
        ArrayList<DataListener> removing = new ArrayList<DataListener>();
        removing.add(listener);
        this.listeners.removeAll(removing);
    }

    /**
     * Prepare the sensing device for data capture. This method has
     * to be called before calling the begin method. If before calling this
     * method there has been another recording activity, the reset method must
     * be called prior to this method. Otherwise an InvalidInvocationError will
     * be thrown.
     */
    public void prepare() {
        if (this.data != null) {
            throw new InvalidInvocationError(
                    CaptureManager.RESET_NOT_CALLED_ERROR_MSG);
        }
        this.data = this.dataSource.prepare();
        for (DataListener listener : this.listeners) {
            this.data.getEvent().addListener(listener);
        }
    }

    /**
     * Start sensing activity. If prepare is not called before calling this,
     * an InvalidInvocationError will be thrown.
     */
    public void begin() {
        if (this.data == null) {
            throw new InvalidInvocationError(
                    CaptureManager.PREPARE_NOT_CALLED_ERROR_MSG);
        }
        this.data.getEvent().startEvent();
        this.dataSource.begin();
    }

    /**
     * Unlike prepare and begin. stop can be called anytime. However if there
     * was no sensing activity before this, null will be returned.
     *
     * @return The data object that contains information of sensing result.
     * Or null if no sensing activity has been initiated.
     */
    public DataWithEvent stop() {
        this.dataSource.stop();
        try {
            this.data.getEvent().stopEvent();
        } catch (InterruptedException e) {
            Log.e(
                    CaptureManager.LOG_TAG,
                    CaptureManager.STOP_INTERRUPTED_EXCEPTION_MSG,
                    e
            );
        }
        return data;
    }

    /**
     * Unlike prepare and begin. reset can be called anytime. It simply reset
     * the sensing device to the default state.
     */
    public void reset() {
        this.data = null;
        this.dataSource.reset();
    }

    public GeneralDevice getDevice(){
        return this.dataSource;
    }
}
