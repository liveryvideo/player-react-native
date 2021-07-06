import * as React from 'react';

import { NativeModules, StyleSheet, Text, View } from 'react-native';
import LiveryReactNativeViewManager from '@exmg/livery-react-native';

const {LiveryPlayer} = NativeModules;

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
      <LiveryReactNativeViewManager 
        streamId={streamId} 
        style={styles.box} 
        onPlaybackStateDidChange={ 
          (event) => { console.log("video state", event.nativeEvent) }
         }
         onGetCustomMessageValue={ 
          (event) => { 
            console.log("onGetCustomMessageValue", event.nativeEvent) 
            const name = event.nativeEvent['name']
            const arg = event.nativeEvent['arg']
            if (arg !== null) {
              LiveryPlayer.sendResponseToInteractiveBridge(name, 'react got this with arg: ' + arg);
            } else {
              LiveryPlayer.sendResponseToInteractiveBridge(name, 'react got this');
            }
          }
         }
      />

      <Text>
          
      </Text>
    
      <Text
        onPress={() => {
          console.log('Play');
            LiveryPlayer.play();
        }}
      >
        Play
      </Text>

      <Text>
          
      </Text>

      <Text
        onPress={() => {
          console.log('Pause');
          LiveryPlayer.pause();
        }}
      >
        Pause
      </Text>

      <Text>
        
      </Text>

      <Text
        onPress={() => {
          console.log('Stop');
          LiveryPlayer.stop();
        }}
      >
        Stop
      </Text>

      <Text>
          
      </Text>

      <Text
        onPress={() => {
          console.log('Send Custom Message');
          LiveryPlayer.sendInteractiveBridgeCustomCommand('test', 'react arg', (error: any, result: any) => {
            if (error !== null) {
              console.log('Send Custom Message error:', error);
            } else {
              console.log('Send Custom Message result:', result);
            }
          });
        }}
      >
        Send Custom Message
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
