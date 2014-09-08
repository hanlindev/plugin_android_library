package com.sana.android.plugin.exception.hardware;

import com.sana.android.plugin.hardware.Feature;

/**
 * Created by MoeMoeMashiro on 9/9/2014.
 */
public class FeatureNotFoundException extends Exception {
    public FeatureNotFoundException(Feature feature) {
        super(String.format("Feature %s is not found.", feature.toCommonName()));
    }
}
