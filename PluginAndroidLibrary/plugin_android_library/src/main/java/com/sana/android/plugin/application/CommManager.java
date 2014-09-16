package com.sana.android.plugin.application;

import android.content.Intent;
import android.net.Uri;

import com.sana.android.plugin.communication.IntentAction;
import com.sana.android.plugin.communication.MimeType;
import com.sana.android.plugin.hardware.Feature;

import java.util.HashMap;

public class CommManager {
    private Intent intent;
    private HashMap<String, IntentAction> actionMap;

    public CommManager() {
        this.actionMap = new HashMap<>();
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
        String action = intent.getAction();
        this.actionMap.get(action).react(this);
    }

    public Intent getIntent() {
        // TODO
        return null;
    }

    public Feature getFeature() {
        // TODO
        return null;
    }

    public MimeType getMimeType() {
        // TODO
        return null;
    }

    public void sendData(Uri uri) {
        // TODO
    }

    public void sendData(String data) {
        // TODO
    }
}
