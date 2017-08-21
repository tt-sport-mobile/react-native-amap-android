package com.tomtom.android.react.amaps;

import android.app.Activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapsPackage implements ReactPackage {
    public MapsPackage(Activity activity) {
    } // backwards compatibility

    public MapsPackage() {
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new AMapModule(reactContext));
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        AMapCalloutManager calloutManager = new AMapCalloutManager();
        AMapMarkerManager annotationManager = new AMapMarkerManager();
        AMapPolylineManager polylineManager = new AMapPolylineManager(reactContext);
        AMapManager mapManager = new AMapManager(reactContext);

        return Arrays.<ViewManager>asList(
                calloutManager,
                annotationManager,
                polylineManager,
                mapManager);
    }
}
