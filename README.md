# Livery components for React Native

This package provides components for using the Livery player with React Native.

### Required credentials

To install this package, you'll need access to the following:

- The Livery Jitpack repository for Android development. Request credentials from
  Livery, then place them in your Gradle user properties
  (`~/.gradle/gradle.properties`, create if necessary) as follows:

```
authToken=...
```

- The Livery CocoaPods repository. Request credentials from Ex Machina, then
  place them in your `~/.netrc` (create if necessary) as follows:

```
machine sdk-ios-binaries.liveryvideo.com
  login YOUR_USERNAME
  password YOUR_PASSWORD
```

### Installation

Add the package as a dependency to your React Native project.

```bash
yarn add @exmg/livery-react-native
```

For React Native 0.59 and below, you also need to link the package.

```bash
react-native link @exmg/livery-react-native
```

For Android development, you need to add the Livery repository to your project
`build.gradle`.

```
allprojects {
   repositories {
      //...
      google()
      jcenter()
      maven {
         url 'https://jitpack.io'
         credentials { username authToken }
      }
   }
}
```

For iOS development, you need to add the Livery repository to your
`ios/Podfile`.

```ruby
# Livery private repository
source 'https://github.com/exmg/livery-sdk-ios-podspec.git'
# Default CocoaPods repository
source 'https://cdn.cocoapods.org/'
```

Then (re)run CocoaPods:

```bash
cd ios/
pod install
```

### Player usage

```jsx
import Player from '@exmg/livery-react-native';
```

Now create a player anywhere in your app using a stream id.

```jsx
const MyView = () => <Player streamId={'streamId'} />;
```
