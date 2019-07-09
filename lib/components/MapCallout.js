//@flow
import React from 'react';
import {
  StyleSheet,
  requireNativeComponent,
  ViewPropTypes,
} from 'react-native';
import PropTypes from 'prop-types';

const propTypes = {
  ...ViewPropTypes,
  tooltip: PropTypes.bool,
  onPress: PropTypes.func,
};

const defaultProps = {
  tooltip: false,
};

class MapCallout extends React.Component {
  render() {
    return <AMapCallout {...this.props} style={[styles.callout, this.props.style]} />;
  }
}

MapCallout.propTypes = propTypes;
MapCallout.defaultProps = defaultProps;

const styles = StyleSheet.create({
  callout: {
    position: 'absolute',
  },
});

var AMapCallout = requireNativeComponent('AMapCallout', MapCallout);

module.exports = MapCallout;
