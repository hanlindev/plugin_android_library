package com.sana.android.plugin.hardware;

import android.content.pm.PackageManager;

/**
 * Created by Chen Xi on 9/2/2014.
 */
public class FeatureChecker {
    private PackageManager pm;

    public FeatureChecker(PackageManager pm) {
        this.pm = pm;
    }
    public boolean isFeatureAvailable(Feature feature) {
        return this.packageManagerHasFeature(feature);
    }

    private boolean packageManagerHasFeature(Feature feature) {
        return this.pm.hasSystemFeature(feature.toString());
    }
}

