import * as React from 'react';

import { StyleSheet, Text, View } from 'react-native';
import LiveryReactNativeViewManager from '@exmg/livery-react-native';

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
      />
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
