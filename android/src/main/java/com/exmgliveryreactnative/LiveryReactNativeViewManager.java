package com.exmgliveryreactnative;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.exmg.livery.LiveryPlayerListener;
import tv.exmg.livery.LiveryControlsOptions;
import tv.exmg.livery.LiveryPlayerOptions;
import tv.exmg.livery.LiveryPlayerState;
import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiveryQuality;
import tv.exmg.livery.LiveryResizeMode;

import tv.exmg.livery.LiverySDK;
import tv.exmg.livery.interactivebridge.LiveryInteractiveBridge;

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
        view.setDebugModeEnabled(true);

        view.registerListener(new LiveryPlayerListener() {
          @Override
          public void onPlayerStateChanged(LiveryPlayerState playerState) {
            Log.d("[LiveryPlayerListener]", "onPlayerStateChanged " + playerState);

            WritableMap event = Arguments.createMap();
            event.putString("playbackState", playerState.toString());
            reactContext.getJSModule(RCTEventEmitter.class)
              .receiveEvent(view.getId(), "onPlaybackStateDidChange", event);
          }

          @Override
          public void onActiveQualityChanged(LiveryQuality quality) {
            Log.d("[LiveryPlayerListener]", "onActiveQualityChanged " + quality.label);
            WritableMap event = Arguments.createMap();
            event.putString("activeQuality", quality.label);
            reactContext.getJSModule(RCTEventEmitter.class)
              .receiveEvent(view.getId(), "onActiveQualityDidChange", event);
          }

          @Override
          public void onPlayerError(Exception e) {
            Log.d("[LiveryPlayerListener]", "onPlayerError " + e.toString());
            WritableMap event = Arguments.createMap();
            event.putString("error", e.toString());
            reactContext.getJSModule(RCTEventEmitter.class)
              .receiveEvent(view.getId(), "onPlayerError", event);
          }
        });

      view.setInteractiveBridgeCustomCommandListener(new LiveryInteractiveBridge.CustomCommandListener() {
        @Override
        public void onMessage(@NonNull String name, @Nullable String arg, @Nullable LiveryInteractiveBridge.CustomCommandResultCallback customCommandResultCallback) {
          Log.d("[CustomCommandListener]","onMessage name: [" + name + "] arg: [" + arg + "]");

          player.addMessage(name, customCommandResultCallback);

          WritableMap event = Arguments.createMap();
          event.putString("name", name);
          event.putString("arg", arg);
          reactContext.getJSModule(RCTEventEmitter.class)
            .receiveEvent(view.getId(), "onGetCustomMessageValue", event);
        }
      });

        player = new LiveryPlayer(view);

        // Forward lifecycle events to the player.
        connectPlayerLifecycle(view, reactContext);

        return view;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put(
        "onPlaybackStateDidChange",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of(
            "bubbled",
            "onPlaybackStateDidChange"
          )
        )
      );
      map.put(
        "onActiveQualityDidChange",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of(
            "bubbled",
            "onActiveQualityDidChange"
          )
        )
      );
      map.put(
        "onPlayerError",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of(
            "bubbled",
            "onPlayerError"
          )
        )
      );
      map.put(
        "onGetCustomMessageValue",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of(
            "bubbled",
            "onGetCustomMessageValue"
          )
        )
      );
      return map;
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
