package com.liveryvideoplayerreactnative;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;

import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.interactivebridge.LiveryInteractiveBridge;

public class LiveryInteractiveBridgeForwarder extends Forwarder implements LiveryInteractiveBridge.CustomCommandListener {

    LiveryInteractiveBridgeForwarder(ReactContext context, LiveryPlayerView playerView) {
        super("InteractiveBridgeForwarder", context, playerView);
    }


    @Override
    public void onMessage(@NonNull String name, @Nullable String arg, @Nullable LiveryInteractiveBridge.CustomCommandResultCallback customCommandResultCallback) {
        event("onGetCustomMessageValue")
                .putString("name", name)
                .putString("arg", arg)
                .emit();
    }
}
