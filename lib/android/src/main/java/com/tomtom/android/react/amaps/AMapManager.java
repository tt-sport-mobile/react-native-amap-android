package com.tomtom.android.react.amaps;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

import javax.annotation.Nullable;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;


public class AMapManager extends ViewGroupManager<AMapView> {

    private static final String REACT_CLASS = "AMap";
    private static final int ANIMATE_TO_REGION = 1;
    private static final int ANIMATE_TO_COORDINATE = 2;
    private static final int FIT_TO_ELEMENTS = 3;
    private static final int FIT_TO_SUPPLIED_MARKERS = 4;
    private static final int FIT_TO_COORDINATES = 5;

    private final Map<String, Integer> MAP_TYPES = MapBuilder.of(
            "standard", AMap.MAP_TYPE_NORMAL,
            "satellite", AMap.MAP_TYPE_SATELLITE
    );

    private final ReactApplicationContext appContext;
    private AMapMarkerManager markerManager;
    private AMapPolylineManager polylineManager;
    private AMapOptions aMapOptions;

    public static final LatLng BEIJING = new LatLng(26.0589209,119.340475);

    public AMapManager(ReactApplicationContext context) {
        this.appContext = context;
        this.aMapOptions = new AMapOptions();
        this.markerManager = markerManager;
        this.polylineManager = polylineManager;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AMapView createViewInstance(ThemedReactContext context) {
        return new AMapView(context, this.appContext, this, aMapOptions);
    }

    private void emitMapError(ThemedReactContext context, String message, String type) {
        WritableMap error = Arguments.createMap();
        error.putString("message", message);
        error.putString("type", type);

        context
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onError", error);
    }

    @ReactProp(name = "region")
    public void setRegion(AMapView view, ReadableMap region) throws AMapException {
        view.setRegion(region);
    }

    @ReactProp(name = "mapType", defaultInt = AMap.MAP_TYPE_NORMAL)
    public void setMapType(AMapView view, @Nullable String mapType) {
        int typeId = MAP_TYPES.get(mapType);
        view.map.setMapType(typeId);
    }

    @ReactProp(name = "showsUserLocation", defaultBoolean = false)
    public void setShowsUserLocation(AMapView view, boolean showUserLocation) {
        view.setShowsUserLocation(showUserLocation);
    }

    @ReactProp(name = "showsMyLocationButton", defaultBoolean = true)
    public void setShowsMyLocationButton(AMapView view, boolean showMyLocationButton) {
        view.setShowsMyLocationButton(showMyLocationButton);
    }

    // This is a private prop to improve performance of panDrag by disabling it when the callback is not set
    @ReactProp(name = "handlePanDrag", defaultBoolean = false)
    public void setHandlePanDrag(AMapView view, boolean handlePanDrag) {
        view.setHandlePanDrag(handlePanDrag);
    }

    @ReactProp(name = "showsCompass", defaultBoolean = false)
    public void setShowsCompass(AMapView view, boolean showsCompass) {
        view.map.getUiSettings().setCompassEnabled(showsCompass);
    }

    @ReactProp(name = "scrollEnabled", defaultBoolean = false)
    public void setScrollEnabled(AMapView view, boolean scrollEnabled) {
        view.map.getUiSettings().setScrollGesturesEnabled(scrollEnabled);
    }

    @ReactProp(name = "zoomEnabled", defaultBoolean = false)
    public void setZoomEnabled(AMapView view, boolean zoomEnabled) {
        view.map.getUiSettings().setZoomGesturesEnabled(zoomEnabled);
        view.map.getUiSettings().setZoomControlsEnabled(zoomEnabled);
    }

    @ReactProp(name = "loadingEnabled", defaultBoolean = false)
    public void setLoadingEnabled(AMapView view, boolean loadingEnabled) {
        view.enableMapLoading(loadingEnabled);
    }

    @ReactProp(name = "moveOnMarkerPress", defaultBoolean = true)
    public void setMoveOnMarkerPress(AMapView view, boolean moveOnPress) {
        view.setMoveOnMarkerPress(moveOnPress);
    }

    @ReactProp(name = "loadingBackgroundColor", customType = "Color")
    public void setLoadingBackgroundColor(AMapView view, @Nullable Integer loadingBackgroundColor) {
        view.setLoadingBackgroundColor(loadingBackgroundColor);
    }

    @ReactProp(name = "loadingIndicatorColor", customType = "Color")
    public void setLoadingIndicatorColor(AMapView view, @Nullable Integer loadingIndicatorColor) {
        view.setLoadingIndicatorColor(loadingIndicatorColor);
    }

    @Override
    public void receiveCommand(AMapView view, int commandId, @Nullable ReadableArray args) {
        Integer duration;
        Double lat;
        Double lng;
        Double lngDelta;
        Double latDelta;
        ReadableMap region;

        switch (commandId) {
            case ANIMATE_TO_REGION:
                region = args.getMap(0);
                duration = args.getInt(1);
                lng = region.getDouble("longitude");
                lat = region.getDouble("latitude");
                lngDelta = region.getDouble("longitudeDelta");
                latDelta = region.getDouble("latitudeDelta");
                LatLngBounds bounds = null;
                try {
                    bounds = new LatLngBounds(
                            new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                            new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
                    );
                } catch (AMapException e) {
                    e.printStackTrace();
                }
                view.animateToRegion(bounds, duration);
                break;

            case ANIMATE_TO_COORDINATE:
                region = args.getMap(0);
                duration = args.getInt(1);
                lng = region.getDouble("longitude");
                lat = region.getDouble("latitude");
                view.animateToCoordinate(new LatLng(lat, lng), duration);
                break;

            case FIT_TO_ELEMENTS:
                view.fitToElements(args.getBoolean(0));
                break;

            case FIT_TO_SUPPLIED_MARKERS:
                view.fitToSuppliedMarkers(args.getArray(0), args.getBoolean(1));
                break;
            case FIT_TO_COORDINATES:
                view.fitToCoordinates(args.getArray(0), args.getMap(1), args.getBoolean(2));
                break;
        }
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> map = MapBuilder.of(
                "onMapReady", MapBuilder.of("registrationName", "onMapReady"),
                "onPress", MapBuilder.of("registrationName", "onPress"),
                "onLongPress", MapBuilder.of("registrationName", "onLongPress"),
                "onMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"),
                "onMarkerSelect", MapBuilder.of("registrationName", "onMarkerSelect"),
                "onMarkerDeselect", MapBuilder.of("registrationName", "onMarkerDeselect"),
                "onCalloutPress", MapBuilder.of("registrationName", "onCalloutPress")
        );

        map.putAll(MapBuilder.of(
                "onMarkerDragStart", MapBuilder.of("registrationName", "onMarkerDragStart"),
                "onMarkerDrag", MapBuilder.of("registrationName", "onMarkerDrag"),
                "onMarkerDragEnd", MapBuilder.of("registrationName", "onMarkerDragEnd"),
                "onPanDrag", MapBuilder.of("registrationName", "onPanDrag")
        ));

        return map;
    }

    @Override
    @Nullable
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "animateToRegion", ANIMATE_TO_REGION,
                "animateToCoordinate", ANIMATE_TO_COORDINATE,
                "fitToElements", FIT_TO_ELEMENTS,
                "fitToSuppliedMarkers", FIT_TO_SUPPLIED_MARKERS,
                "fitToCoordinates", FIT_TO_COORDINATES
        );
    }

    @Override
    public void addView(AMapView parent, View child, int index) {
        parent.addFeature(child, index);
    }

    @Override
    public int getChildCount(AMapView view) {
        return view.getFeatureCount();
    }

    @Override
    public View getChildAt(AMapView view, int index) {
        return view.getFeatureAt(index);
    }

    @Override
    public void removeViewAt(AMapView parent, int index) {
        parent.removeFeatureAt(index);
    }

    @Override
    public void updateExtraData(AMapView view, Object extraData) {
        view.updateExtraData(extraData);
    }

    void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
        context.getJSModule(RCTEventEmitter.class)
            .receiveEvent(view.getId(), name, data);
    }



    @Override
    public void onDropViewInstance(AMapView view) {
        view.doDestroy();
        super.onDropViewInstance(view);
    }

}
