import * as React from 'react';

import { StyleSheet, Text, View } from 'react-native';
import Player from '@exmg/livery-react-native';

export default function App() {
  const [streamId, setStreamId] = React.useState('ABC');

  return (
    <View style={styles.container}>
      <Text
        onPress={() => {
          console.log('PRESSED');
          setStreamId('5ddb98f5e4b0937e6a4507f2');
        }}
      >
        Press me
      </Text>
      <Player
        streamId={streamId}
        style={styles.box}
        onPlaybackStateDidChange={(event) => {
          console.log('video state', event.nativeEvent);
        }}
        onActiveQualityDidChange={(event) => {
            console.log("onActiveQualityDidChange", event.nativeEvent)
        }}
        onPlayerError={(event) => {
            console.log("onPlayerError", event.nativeEvent)
        }}
        onPlayerDidRecover={(event) => {
          console.log("onPlayerDidRecover", event.nativeEvent)
        }}
        onProgressDidChange={(event) => {
          console.log("onProgressDidChange", event.nativeEvent)
        }}
        onQualitiesDidChange={(event) => {
          console.log("onQualitiesDidChange", event.nativeEvent)
        }}
        onGetCustomMessageValue={(event) => {
          console.log('onGetCustomMessageValue', event.nativeEvent);
          const name = event.nativeEvent['name'];
          const arg = event.nativeEvent['arg'];
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

      <Text></Text>

      <Text
        onPress={() => {
          console.log('Play');
          Player.play();
        }}
      >
        Play
      </Text>

      <Text></Text>

      <Text
        onPress={() => {
          console.log('Pause');
          Player.pause();
        }}
      >
        Pause
      </Text>

      <Text></Text>

      <Text
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
      >
        Send Custom Message
      </Text>

      <Text></Text>

      <Text
        onPress={() => {
          console.log('Set Interactive URL');
          Player.setInteractiveURL('https://interactive.liveryvideo.com');
        }}
      >
        Test interactive bridge
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 350,
    height: 200,
    marginVertical: 20,
  },
});
