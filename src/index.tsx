import { PureComponent, SyntheticEvent } from 'react';
import {
  NativeModules,
  requireNativeComponent,
  ViewProps,
  ViewStyle,
} from 'react-native';
import * as React from 'react';

const { LiveryPlayer } = NativeModules;

interface VideoState {
  playbackState: string;
}

interface LiveryProps {
  streamId: string;
  style: ViewStyle;
  onPlaybackStateDidChange?: (
    event: SyntheticEvent<unknown, VideoState>
  ) => void;
  onActiveQualityDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onPlayerError?: (event: SyntheticEvent<unknown, any>) => void;
  onPlayerDidRecover?: (event: SyntheticEvent<unknown, any>) => void;
  onProgressDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onQualitiesDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onGetCustomMessageValue?: (event: SyntheticEvent<unknown, any>) => void;
}

type LiveryReactNativeProps = ViewProps & LiveryProps;

const LiveryReactNativeViewManager =
  requireNativeComponent<LiveryReactNativeProps>('LiveryReactNativeView');

class Player extends PureComponent<LiveryReactNativeProps> {
  constructor(props: LiveryReactNativeProps) {
    super(props);
  }

  onPlaybackStateDidChange = (
    event: SyntheticEvent<unknown, VideoState>
  ): void => {
    const { onPlaybackStateDidChange } = this.props;

    if (onPlaybackStateDidChange) {
      onPlaybackStateDidChange(event);
    }
  };

  onGetCustomMessageValue = (event: SyntheticEvent<unknown, any>): void => {
    const { onGetCustomMessageValue } = this.props;

    if (onGetCustomMessageValue) {
      onGetCustomMessageValue(event);
    }
  };

  static play = LiveryPlayer.play;
  static pause = LiveryPlayer.pause;
  static sendInteractiveBridgeCustomCommand =
    LiveryPlayer.sendInteractiveBridgeCustomCommand;
  static sendResponseToInteractiveBridge =
    LiveryPlayer.sendResponseToInteractiveBridge;
  static setInteractiveURL = LiveryPlayer.setInteractiveURL;

  render() {
    return (
      <LiveryReactNativeViewManager
        {...this.props}
        onPlaybackStateDidChange={this.onPlaybackStateDidChange}
        onGetCustomMessageValue={this.onGetCustomMessageValue}
      />
    );
  }
}

export default Player;
