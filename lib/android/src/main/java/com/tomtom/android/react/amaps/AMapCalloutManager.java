package com.tomtom.android.react.amaps;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;

public class AMapCalloutManager extends ViewGroupManager<AMapCallout> {

    @Override
    public String getName() {
        return "AMapCallout";
    }

    @Override
    public AMapCallout createViewInstance(ThemedReactContext context) {
        return new AMapCallout(context);
    }

    @ReactProp(name = "tooltip", defaultBoolean = false)
    public void setTooltip(AMapCallout view, boolean tooltip) {
        view.setTooltip(tooltip);
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of("onPress", MapBuilder.of("registrationName", "onPress"));
    }

    @Override
    public void updateExtraData(AMapCallout view, Object extraData) {
        // This method is called from the shadow node with the width/height of the rendered
        // marker view.
        //noinspection unchecked
        Map<String, Float> data = (Map<String, Float>) extraData;
        float width = data.get("width");
        float height = data.get("height");
        view.width = (int) width;
        view.height = (int) height;
    }

}
