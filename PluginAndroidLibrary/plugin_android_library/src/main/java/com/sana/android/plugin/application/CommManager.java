package com.sana.android.plugin.application;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;

import com.sana.android.plugin.communication.IntentAction;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.Feature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommManager {
    private Intent intent;
    private HashMap<String, IntentAction> actionMap;
    private static CommManager sharedInstance;

    private CommManager() {
        this.actionMap = new HashMap<String, IntentAction>();
    }

    public static CommManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new CommManager();
        }
        return sharedInstance;
    }

    public void addAction(String actionValue, IntentAction action) {
        this.actionMap.put(actionValue, action);
    }

    public void removeAction(String actionValue) {
        this.actionMap.remove(actionValue);
    }

    public void clearActions() {
        this.actionMap.clear();
    }

    public void respondToIntent(Intent intent) {
        this.intent = intent;
        String action = intent.getAction();
        if (this.actionMap.get(action) != null) {
            this.actionMap.get(action).react(this);
        }
    }

    public Intent getIntent() {
        return this.intent;
    }

    public MimeType getMimeType() {
        String value = intent.getType();
        MimeType mimeType = MimeType.fromString(value);
        return mimeType;
    }

    public Uri getUri() {
        //Intent intent = this.intent;
        if (this.getIntent() == null) {
            return null;
        }
        return this.getIntent().getData();
        //return this.intent.getData();
    }

    /**
     *
     * @param key Key of control parameter
     * @return Value of control parameter with key name or null if the control parameter is not exist
     */
    public String getControlParameter(String key) {
        return this.intent.getStringExtra(key);
    }

    /**
     *
     * @param keys keys of all control parameters
     * @return a Map with all control parameter key-value pairs
     */
    public Map<String, String> getAllControlParameters(Collection<String> keys) {
        HashMap<String, String> result = new HashMap<>();
        for (String key : keys) {
            if (this.getControlParameter(key) != null) {
                result.put(key, this.getControlParameter(key));
            }
        }
        return result;
    }

    /*
        Send binary data to Sana
     */
    public void sendData(Activity sender) {
        Uri uri = this.intent.getData();
        String type = this.intent.getType();
        Intent dataIntent = new Intent();
        dataIntent.setDataAndType(uri, type);
        sender.setResult(Activity.RESULT_OK, dataIntent);
        sender.finish();
    }

    /*
        Send plain text data to Sana
     */
    public void sendData(Activity sender, String data) {
        Uri uri = this.intent.getData();
        System.out.println(uri);
        uri = uri.buildUpon().fragment(data).build();
        String type = this.intent.getType();
        Intent dataIntent = new Intent();
        dataIntent.setDataAndType(uri, type);
//        dataIntent.putExtra(Intent.EXTRA_TEXT, data);
        sender.setResult(Activity.RESULT_OK, dataIntent);
        sender.finish();
    }
}
