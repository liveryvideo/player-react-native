#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(LiveryReactNativeViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(streamId, NSString)
RCT_EXPORT_VIEW_PROPERTY(onPlaybackStateDidChange, RCTBubblingEventBlock)

@end
