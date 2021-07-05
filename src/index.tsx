import type { SyntheticEvent } from 'react';
import { requireNativeComponent, ViewStyle } from 'react-native';

interface VideoState {
    playbackState: string;
}

type LiveryReactNativeProps = {
  streamId: string;
  style: ViewStyle;
  onPlaybackStateDidChange?: (event: SyntheticEvent<unknown, VideoState>) => void;
  onGetCustomMessageValue?: (event: SyntheticEvent<unknown, any>) => void;
};

export const LiveryReactNativeViewManager =
  requireNativeComponent<LiveryReactNativeProps>('LiveryReactNativeView');

export default LiveryReactNativeViewManager;
