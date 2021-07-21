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

For iOS development, on your `ios/Podfile` do as follows:

- check that iOS version is equal or higher than 12
- add `use_frameworks!`
- remove `use_flipper!()`
- add the Livery repository source

```ruby
platform :ios, '12.0'
use_frameworks!

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

#### Error: Cycle in dependencies between targets

On iOS, if building the app fails with the following error:
`Cycle in dependencies between targets 'YourProjectTarget' and 'FBReactNativeSpec'; building could produce unreliable results.`

Please add the following lines to your App's `Podfile` inside `YourProjectTarget` and re-run `pod install` on the terminal

```ruby
post_install do |installer|
    react_native_post_install(installer)
    
    installer.pods_project.targets.each do |target|
        target.build_configurations.each do |config|
          config.build_settings['BUILD_LIBRARY_FOR_DISTRIBUTION'] = 'YES'
        end
        
        if (target.name&.eql?('FBReactNativeSpec'))
          target.build_phases.each do |build_phase|
            if (build_phase.respond_to?(:name) && build_phase.name.eql?('[CP-User] Generate Specs'))
              target.build_phases.move(build_phase, 0)
            end
          end
        end
      end
  end
```

### Player usage

```jsx
import Player from '@exmg/livery-react-native';
```

Now create a player anywhere in your app using a stream id.

```jsx
const MyView = () => <Player streamId={'streamId'} />;
```
