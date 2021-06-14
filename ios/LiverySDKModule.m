#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <exmg_livery_react_native/exmg_livery_react_native-Swift.h>

/// Interface for global Livery SDK configuration from React Native.
@interface LiverySDKModule : NSObject <RCTBridgeModule>
@end

@implementation LiverySDKModule

RCT_EXPORT_MODULE(LiverySDK);

RCT_EXPORT_METHOD(streamId:(NSString *)streamId)
{
  LiverySDKProvider.streamId = streamId;
}

@end
