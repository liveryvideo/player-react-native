package com.exmgliveryreactnative;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
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

    private LiveryPlayer player;

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

        player = new LiveryPlayer(view);

        // Forward lifecycle events to the player.
        connectPlayerLifecycle(view, reactContext);

        return view;
    }

    @ReactProp(name = "streamId")
    public void setStreamId(LiveryPlayerView view, String streamId) {
      player.setStreamId(streamId);
    }

  /**
   * Connect a player to the React Native lifecycle.
   */
  private static void connectPlayerLifecycle(final LiveryPlayerView view, final ThemedReactContext context) {
    // Forward lifecycle events to the view.
    final LifecycleEventListener lifecycleEventListener = new LifecycleEventListener() {
      @Override
      public void onHostResume() {
        view.onResume();
      }

      @Override
      public void onHostPause() {
        view.onPause();
      }

      @Override
      public void onHostDestroy() {
        view.onDestroy();
      }
    };

    // Install the lifecycle listener, and make sure we properly clean up the player.
    view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
      @Override
      public void onViewAttachedToWindow(View v) {
        context.addLifecycleEventListener(lifecycleEventListener);
      }

      @Override
      public void onViewDetachedFromWindow(View v) {
        context.removeLifecycleEventListener(lifecycleEventListener);
      }
    });
  }
}
