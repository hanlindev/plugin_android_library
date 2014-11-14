package sana.com.plugin.mockApp;

import android.util.Log;
/**
 * Created by Mia on 17/10/14.
 */
public class AppLog {
    private static final String APP_TAG = "AudioRecorder";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}
