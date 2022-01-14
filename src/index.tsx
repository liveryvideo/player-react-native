import { PureComponent, SyntheticEvent } from 'react';
import {
  NativeModules,
  requireNativeComponent,
  ViewProps,
  ViewStyle,
} from 'react-native';
import * as React from 'react';

const { LiveryPlayer } = NativeModules;

interface PlaybackState {
  playbackState: string;
}

export enum PlaybackControlState {
  Pause = 0,
  Play = 1,
}

interface PlayerState {
  streamId: string | null;
  playbackControlState: PlaybackControlState | undefined;
  interactiveURL?: string | undefined;
}

interface PlayerProps {
  style: ViewStyle;
  onPlaybackStateDidChange?: (event: SyntheticEvent<unknown, PlaybackState>) => void;
  onPlaybackRateDidChanged?: (event: SyntheticEvent<unknown, number>) => void;
  onActiveQualityDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onPlayerError?: (event: SyntheticEvent<unknown, any>) => void;
  onPlayerDidRecover?: (event: SyntheticEvent<unknown, any>) => void;
  onProgressDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onQualitiesDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onSelectedQualityDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onSourceDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onTimeDidUpdate?: (event: SyntheticEvent<unknown, any>) => void;
  onVolumeDidChange?: (event: SyntheticEvent<unknown, any>) => void;
  onGetCustomMessageValue?: (event: SyntheticEvent<unknown, any>) => void;
}

type LiveryReactNativeProps = ViewProps & PlayerProps & PlayerState;

const LiveryReactNativeViewManager =
  requireNativeComponent<LiveryReactNativeProps>('LiveryReactNativeView');

class Player extends PureComponent<LiveryReactNativeProps> {
  constructor(props: LiveryReactNativeProps) {
    super(props);
  }

  onPlaybackStateDidChange = (
    event: SyntheticEvent<unknown, PlaybackState>
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

  static sendInteractiveBridgeCustomCommand =
    LiveryPlayer.sendInteractiveBridgeCustomCommand;
  static sendResponseToInteractiveBridge =
    LiveryPlayer.sendResponseToInteractiveBridge;

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
