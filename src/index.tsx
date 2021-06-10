import { requireNativeComponent, ViewStyle } from 'react-native';

type LiveryReactNativeProps = {
  color: string;
  style: ViewStyle;
};

export const LiveryReactNativeViewManager = requireNativeComponent<LiveryReactNativeProps>(
'LiveryReactNativeView'
);

export default LiveryReactNativeViewManager;
