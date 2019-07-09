//@flow
import React  from 'react';
import {
  EdgeInsetsPropType,
  Platform,
  Animated,
  requireNativeComponent,
  NativeModules,
  ColorPropType,
  findNodeHandle,
  ViewPropTypes,
} from 'react-native';
import PropTypes from 'prop-types';

import MapMarker from './MapMarker';
import MapPolyline from './MapPolyline';
import MapCallout from './MapCallout';
import AnimatedRegion from './AnimatedRegion';

const MAP_TYPES = {
  STANDARD: 'standard',
  SATELLITE: 'satellite',
};

const  viewConfig = {
  uiViewClassName: 'AMap',
  validAttributes: {
    region: true,
  },
};

const  propTypes = {
  ...ViewPropTypes,

  /**
   * If `true` the map will be showing a loading indicator
   * Default value is `false`.
   *
   */
  loadingEnabled: PropTypes.bool,

  /**
   * A Boolean indicating whether on marker press the map will move to the pressed marker
   * Default value is `true`
   *
   * @platform android
   */
  moveOnMarkerPress: PropTypes.bool,

  /**
   * If `false` hide the button to move map to the current user's location.
   * Default value is `true`.
   *
   * @platform android
   */
  showsMyLocationButton: PropTypes.bool,

  /**
   * Loading indicator color while generating map cache image or loading the map
   * Default color is gray color for iOS, theme color for Android.
   *
   */
  loadingIndicatorColor: ColorPropType,

  /**
   * Loading background color while generating map cache image or loading the map
   * Default color is light gray.
   *
   */
  loadingBackgroundColor: ColorPropType,

  /**
   * [apiKey amap's apiKey]
   * @type {[type]}
   */
  apiKey: PropTypes.string,

  /**
   * If `true` the app will ask for the user's location and focus on it.
   * Default value is `false`.
   *
   * **NOTE**: You need to add NSLocationWhenInUseUsageDescription key in
   * Info.plist to enable geolocation, otherwise it is going
   * to *fail silently*!
   */
  showsUserLocation: PropTypes.bool,

  /**
   * If `false` points of interest won't be displayed on the map.
   * Default value is `true`.
   *
   */
  showsPointsOfInterest: PropTypes.bool,

  /**
   * If `false` compass won't be displayed on the map.
   * Default value is `true`.
   *
   * @platform ios
   */
  showsCompass: PropTypes.bool,

  /**
   * If `false` the user won't be able to pinch/zoom the map.
   * Default value is `true`.
   *
   */
  zoomEnabled: PropTypes.bool,

  zoomLevel: PropTypes.number,
  /**
   * If `false` the user won't be able to pinch/rotate the map.
   * Default value is `true`.
   *
   */
  rotateEnabled: PropTypes.bool,

  /**
   * If `false` the user won't be able to change the map region being displayed.
   * Default value is `true`.
   *
   */
  scrollEnabled: PropTypes.bool,

  /**
   * If `false` the user won't be able to adjust the camera’s pitch angle.
   * Default value is `true`.
   *
   */
  pitchEnabled: PropTypes.bool,

  /**
   * A Boolean indicating whether the map shows scale information.
   * Default value is `false`
   *
   */
  showsScale: PropTypes.bool,

  /**
   * A Boolean indicating whether the map displays extruded building information.
   * Default value is `true`.
   */
  showsBuildings: PropTypes.bool,

  /**
   * A Boolean value indicating whether the map displays traffic information.
   * Default value is `false`.
   */
  showsTraffic: PropTypes.bool,

  /**
   * A Boolean indicating whether indoor maps should be enabled.
   * Default value is `false`
   *
   * @platform android
   */
  showsIndoors: PropTypes.bool,

  /**
  * The map type to be displayed.
  *
  * - standard: standard road map (default)
  * - satellite: satellite view
  * - hybrid: satellite view with roads and points of interest overlayed
  * - terrain: topographic view
  * - none: no base map
  */
  mapType: PropTypes.oneOf(Object.values(MAP_TYPES)),

  userTrackingMode: PropTypes.oneOf([
    'none',
    'follow',
    'followWithHeading',
  ]),

  /**
   * The region to be displayed by the map.
   *
   * The region is defined by the center coordinates and the span of
   * coordinates to display.
   */
  region: PropTypes.shape({
    /**
     * Coordinates for the center of the map.
     */
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,

    /**
     * Difference between the minimun and the maximum latitude/longitude
     * to be displayed.
     */
    latitudeDelta: PropTypes.number,
    longitudeDelta: PropTypes.number,
  }),

  /**
   * The initial region to be displayed by the map.  Use this prop instead of `region`
   * only if you don't want to control the viewport of the map besides the initial region.
   *
   * Changing this prop after the component has mounted will not result in a region change.
   *
   * This is similar to the `initialValue` prop of a text input.
   */
  initialRegion: PropTypes.shape({
    /**
     * Coordinates for the center of the map.
     */
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,

    /**
     * Difference between the minimun and the maximum latitude/longitude
     * to be displayed.
     */
    latitudeDelta: PropTypes.number,
    longitudeDelta: PropTypes.number,
  }),

  /**
   * Maximum size of area that can be displayed.
   *
   * @platform ios
   */
  maxDelta: PropTypes.number,

  /**
   * Minimum size of area that can be displayed.
   *
   * @platform ios
   */
  minDelta: PropTypes.number,

  /**
   * Insets for the map's legal label, originally at bottom left of the map.
   * See `EdgeInsetsPropType.js` for more information.
   */
  legalLabelInsets: EdgeInsetsPropType,

  /**
   * Callback that is called continuously when the user is dragging the map.
   */
  onRegionChange: PropTypes.func,

  /**
   * Callback that is called once, when the user is done moving the map.
   */
  onRegionChangeComplete: PropTypes.func,

  /**
   * Callback that is called when user taps on the map.
   */
  onPress: PropTypes.func,

  /**
   * Callback that is called when user makes a "long press" somewhere on the map.
   */
  onLongPress: PropTypes.func,

  /**
   * Callback that is called when a marker on the map is tapped by the user.
   */
  onMarkerPress: PropTypes.func,

  /**
   * Callback that is called when a marker on the map becomes selected. This will be called when
   * the callout for that marker is about to be shown.
   *
   * @platform ios
   */
  onMarkerSelect: PropTypes.func,

  /**
   * Callback that is called when a marker on the map becomes deselected. This will be called when
   * the callout for that marker is about to be hidden.
   *
   * @platform ios
   */
  onMarkerDeselect: PropTypes.func,

  /**
   * Callback that is called when a callout is tapped by the user.
   */
  onCalloutPress: PropTypes.func,

  /**
   * Callback that is called when the user initiates a drag on a marker (if it is draggable)
   */
  onMarkerDragStart: PropTypes.func,

  /**
   * Callback called continuously as a marker is dragged
   */
  onMarkerDrag: PropTypes.func,

  /**
   * Callback that is called when a drag on a marker finishes. This is usually the point you
   * will want to setState on the marker's coordinate again
   */
  onMarkerDragEnd: PropTypes.func,
};

class MapView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      isReady: Platform.OS === 'ios',
    };

    this._onMapReady = this._onMapReady.bind(this);
    this._onChange = this._onChange.bind(this);
    this._onLayout = this._onLayout.bind(this);
  }

  componentWillUpdate(nextProps) {
    const b = nextProps.region;
    if (this.map) {
      this.map.setNativeProps({
        region: b
      });
    }
  }

  componentDidMount() {
    // console.log("MapView.js: in componentDidMount");
    const { isReady } = this.state;
    if (isReady) {
      this._updateStyle();
    }
  }

  _updateStyle() {
    // console.log("MapView.js: in _updateStyle");
    const { customMapStyle } = this.props;
    this.map.setNativeProps({ customMapStyleString: JSON.stringify(customMapStyle) });
  }

  _onMapReady() {
    // console.log("MapView.js: in _onMapReady");
    const { region, initialRegion, onMapReady } = this.props;
    if (region) {
      this.map.setNativeProps({ region });
    } else if (initialRegion) {
      this.map.setNativeProps({ region: initialRegion });
    }
    this._updateStyle();
    this.setState({ isReady: true }, () => {
      if (onMapReady) onMapReady();
    });
  }

  _onLayout(e) {
    // console.log("MapView.js: in _onLayout");
    const { layout } = e.nativeEvent;
    if (!layout.width || !layout.height) return;
    if (this.state.isReady && !this.__layoutCalled) {
      const region = this.props.region || this.props.initialRegion;
      if (region) {
        this.__layoutCalled = true;
        this.map.setNativeProps({ region });
      }
    }
    if (this.props.onLayout) {
      this.props.onLayout(e);
    }
  }

  _onChange(event) {
    this.__lastRegion = event.nativeEvent.region;
    if (event.nativeEvent.continuous) {
      if (this.props.onRegionChange) {
        this.props.onRegionChange(event.nativeEvent.region);
      }
    } else if (this.props.onRegionChangeComplete) {
      this.props.onRegionChangeComplete(event.nativeEvent.region);
    }
  }

  animateToRegion(region, duration) {
    this._runCommand('animateToRegion', [region, duration || 500]);
  }

  animateToCoordinate(latLng, duration) {
    this._runCommand('animateToCoordinate', [latLng, duration || 500]);
  }

  fitToElements(animated) {
    this._runCommand('fitToElements', [animated]);
  }

  fitToSuppliedMarkers(markers, animated) {
    this._runCommand('fitToSuppliedMarkers', [markers, animated]);
  }

  fitToCoordinates(coordinates = [], options) {
    const {
      edgePadding = { top: 0, right: 0, bottom: 0, left: 0 },
      animated = true,
    } = options;

    this._runCommand('fitToCoordinates', [coordinates, edgePadding, animated]);
  }

  _getHandle() {
    return findNodeHandle(this.map);
  }

  _runCommand(name, args) {
    switch (Platform.OS) {
    case 'android':
      NativeModules.UIManager.dispatchViewManagerCommand(
        this._getHandle(),
        NativeModules.UIManager.AMap.Commands[name],
        args
      );
      break;

    default:
      break;
    }
  }

  render() {
    const { region } = this.props;

    if (this.state.isReady) {
      props = {
        ...this.props,
        region: region,
        initialRegion: null,
        onChange: this._onChange,
        onMapReady: this._onMapReady,
        onLayout: this._onLayout,
      };
      props.handlePanDrag = !!props.onPanDrag;
    } else {
      props = {
        style: this.props.style,
        region: region,
        initialRegion: null,
        onChange: this._onChange,
        onMapReady: this._onMapReady,
        onLayout: this._onLayout,
      };
    }

    if (Platform.OS === 'android') {
      return (
        <AMap
          ref={ref => { this.map = ref; }}
          {...props}
        />
      );
    }
  }
}



MapView.propTypes = propTypes;
MapView.viewConfig = viewConfig;

MapView.MAP_TYPES = MAP_TYPES;

const AMap = requireNativeComponent('AMap', MapView, {
  nativeOnly: {
    onChange: true,
    onMapReady: true,
    handlePanDrag: true,
  },
});

MapView.Marker = MapMarker;
MapView.Polyline = MapPolyline;
MapView.Callout = MapCallout;

MapView.Animated = Animated.createAnimatedComponent(MapView);
MapView.AnimatedRegion = AnimatedRegion;


module.exports = MapView;
