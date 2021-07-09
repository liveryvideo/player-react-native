#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(LiveryReactNativeViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(streamId, NSString)

RCT_EXPORT_VIEW_PROPERTY(onPlaybackStateDidChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onActiveQualityDidChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlayerError, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlayerDidRecover, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onProgressDidChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onQualitiesDidChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onSelectedQualityDidChange, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(onGetCustomMessageValue, RCTBubblingEventBlock)

@end
