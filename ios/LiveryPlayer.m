//
//  LiveryPlayer.m
//  liveryvideo-player-react-native
//
//  Created by Jose Nogueira on 05/07/2021.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(LiveryPlayer, NSObject)

RCT_EXTERN_METHOD(play)
RCT_EXTERN_METHOD(pause)
RCT_EXTERN_METHOD(sendInteractiveBridgeCustomCommand: (NSString)name arg:(id)arg callback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(sendResponseToInteractiveBridge: (NSString)forName value:(id)value)
RCT_EXTERN_METHOD(setInteractiveURL: (NSString)interactiveURL)

@end
