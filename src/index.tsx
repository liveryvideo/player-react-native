import { PureComponent, SyntheticEvent } from 'react';
import {
  findNodeHandle,
  requireNativeComponent,
  UIManager,
  ViewProps,
  ViewStyle,
} from 'react-native';
import * as React from 'react';

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
}

interface PlayerProps {
  style: ViewStyle;
  onPlaybackStateDidChange?: (
    event: SyntheticEvent<unknown, PlaybackState>
  ) => void;
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
  onInteractiveBridgeCustomCommandResponse?: (
    event: SyntheticEvent<unknown, any>
  ) => void;
}

type LiveryReactNativeProps = ViewProps & PlayerProps & PlayerState;

const LiveryReactNativeViewName = 'LiveryReactNativeView';

const LiveryReactNativeViewManager =
  requireNativeComponent<LiveryReactNativeProps>(LiveryReactNativeViewName);

class Player extends PureComponent<LiveryReactNativeProps> {
  constructor(props: LiveryReactNativeProps) {
    super(props);
  }

  setInteractiveURL(url: string) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.getViewManagerConfig(LiveryReactNativeViewName).Commands
        .setInteractiveURL,
      [url]
    );
  }

  sendInteractiveBridgeCustomCommand(name: string, arg: string) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.getViewManagerConfig(LiveryReactNativeViewName).Commands
        .sendInteractiveBridgeCustomCommand,
      [name, arg]
    );
  }

  sendResponseToInteractiveBridge(name: string, value: any) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.getViewManagerConfig(LiveryReactNativeViewName).Commands
        .sendResponseToInteractiveBridge,
      [name, value]
    );
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
