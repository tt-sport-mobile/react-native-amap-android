package com.tomtom.android.react.amaps;

import android.app.Activity;
import java.io.IOException;
import java.io.Closeable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class AMapModule extends ReactContextBaseJavaModule {

    public AMapModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "AMapModule";
    }

    public Activity getActivity() {
        return getCurrentActivity();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
