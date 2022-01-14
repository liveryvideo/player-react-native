import * as React from 'react';

import { StyleSheet, Button, View } from 'react-native';
import Player, { PlaybackControlState } from '@liveryvideo/player-react-native';

export default function App() {
  const [streamId, setStreamId] = React.useState<string | null>(null);
  const [playback, setPlayback] = React.useState<PlaybackControlState | undefined>(undefined);
  const [interactiveURL, setInteractiveURL] = React.useState<string | undefined>(undefined);

  return (
    <View style={styles.container}>
      <Player
        streamId={streamId}
        playbackControlState={playback}
        interactiveURL={interactiveURL}
        style={styles.player}
        onPlaybackStateDidChange={(event) => {
          console.log('video state', event.nativeEvent);
        }}
        onActiveQualityDidChange={(event) => {
          console.log('onActiveQualityDidChange', event.nativeEvent);
        }}
        onPlayerError={(event) => {
          console.log('onPlayerError', event.nativeEvent);
        }}
        onPlayerDidRecover={(event) => {
          console.log('onPlayerDidRecover', event.nativeEvent);
        }}
        onProgressDidChange={(event) => {
          console.log('onProgressDidChange', event.nativeEvent);
        }}
        onQualitiesDidChange={(event) => {
          console.log('onQualitiesDidChange', event.nativeEvent);
        }}
        onSelectedQualityDidChange={(event) => {
          console.log('onSelectedQualityDidChange', event.nativeEvent);
        }}
        onSourceDidChange={(event) => {
          console.log('onSourceDidChange', event.nativeEvent);
        }}
        onTimeDidUpdate={(event) => {
          console.log('onTimeDidUpdate', event.nativeEvent);
        }}
        onVolumeDidChange={(event) => {
          console.log('onVolumeDidChange', event.nativeEvent);
        }}
        onGetCustomMessageValue={(event) => {
          console.log('onGetCustomMessageValue', event.nativeEvent);
          const name = event.nativeEvent.name;
          const arg = event.nativeEvent.arg;
          if (arg !== null) {
            Player.sendResponseToInteractiveBridge(
              name,
              'react got this with arg: ' + arg
            );
          } else {
            Player.sendResponseToInteractiveBridge(name, 'react got this');
          }
        }}
      />

      <Button
        onPress={() => {
          console.log('PRESSED');
          setStreamId('5ddb98f5e4b0937e6a4507f2');
        }}
        title="Initialize Player"
      />

      <Button
        onPress={() => {
          console.log('Play');
          setPlayback(PlaybackControlState.Play)
        }}
        title="Play"
      />

      <Button
        onPress={() => {
          console.log('Pause');
          setPlayback(PlaybackControlState.Pause)
        }}
        title="Pause"
      />

      <Button
        onPress={() => {
          console.log('Send Custom Message');
          Player.sendInteractiveBridgeCustomCommand(
            'test',
            'react arg',
            (error: any, result: any) => {
              if (error !== null) {
                console.log('Send Custom Message error:', error);
              } else {
                console.log('Send Custom Message result:', result);
              }
            }
          );
        }}
        title="Send Custom Message"
      />

      <Button
        onPress={() => {
          console.log('Set Interactive URL');
          setInteractiveURL('https://interactive.liveryvideo.com');
        }}
        title="Test interactive bridge"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'space-evenly',
  },
  button: {
    margin: 10,
  },
  player: {
    width: '100%',
    height: '50%',
    marginVertical: 20,
    backgroundColor: '#e0e0e0',
    borderColor: 'black',
    borderWidth: 1,
  },
});
