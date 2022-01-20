package com.liveryvideoplayerreactnative;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;

import java.util.HashMap;

import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.interactivebridge.LiveryInteractiveBridge;

public class LiveryInteractiveBridgeForwarder extends Forwarder implements LiveryInteractiveBridge.CustomCommandListener {

    final HashMap<String, LiveryInteractiveBridge.CustomCommandResultCallback> interactiveBridgeMessages = new HashMap<>();

    LiveryInteractiveBridgeForwarder(ReactContext context, LiveryPlayerView playerView) {
        super("InteractiveBridgeForwarder", context, playerView);
    }


    @Override
    public void onMessage(@NonNull String name, @Nullable String arg, @Nullable LiveryInteractiveBridge.CustomCommandResultCallback customCommandResultCallback) {

        interactiveBridgeMessages.put(name, customCommandResultCallback);

        event("onGetCustomMessageValue")
                .putString("name", name)
                .putString("arg", arg)
                .emit();
    }

    void sendResponse(String name, String value) {
        LiveryInteractiveBridge.CustomCommandResultCallback callback = interactiveBridgeMessages.remove(name);
        if (callback != null) {
            Log.d(TAG, "sendResponseToInteractiveBridge name " + name + " value: " + value);
            callback.result(value);
        }

    }
}
