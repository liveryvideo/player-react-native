package com.exmgliveryreactnative;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import android.util.Log;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;


import tv.exmg.livery.LiveryPlayerListener;
import tv.exmg.livery.LiveryControlsOptions;
import tv.exmg.livery.LiveryPlayerOptions;
import tv.exmg.livery.LiveryPlayerState;
import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiveryQuality;
import tv.exmg.livery.LiveryResizeMode;

import tv.exmg.livery.LiverySDK;

public class LiveryReactNativeViewManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "LiveryReactNativeView";

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public View createViewInstance(ThemedReactContext reactContext) {
        Log.d("CONSOLE LOG: ","create view insteance");
        final LiveryPlayerView view = new LiveryPlayerView(reactContext.getCurrentActivity());


        view.setKeepScreenOn(true);
        view.setTargetLatency(3000);
        view.setDebugModeEnabled(true);



        return view;
    }

    @ReactProp(name = "streamId")
    public void setStreamId(LiveryPlayerView view, String streamId) {
        LiverySDK.getInstance().initialize(streamId, new LiverySDK.StateListener() {
           @Override
           public void stateChanged(LiverySDK.State state) {
            Log.d("CONSOLE LOG: ","STATE CHANGED");
            view.createPlayer();
           }
        });
    }
}
