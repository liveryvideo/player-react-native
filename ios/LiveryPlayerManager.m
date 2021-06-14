#import <React/RCTViewManager.h>
#import <exmg_livery_react_native/exmg_livery_react_native-Swift.h>

/// This class manages instances of `LiveryPlayerView` for React Native.
@interface LiveryPlayerManager : RCTViewManager
@end

@implementation LiveryPlayerManager

RCT_EXPORT_MODULE(LiveryPlayer)
RCT_EXPORT_VIEW_PROPERTY(onVideoStateChanged, RCTBubblingEventBlock)

- (UIView *)view
{
  return [LiveryPlayerView new];
}

@end