package com.liveryvideoplayerreactnative;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.HashMap;
import java.util.Map;

import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiverySDK;

@SuppressWarnings({"unused", "Convert2Lambda"})
public class LiveryReactNativeViewManager extends SimpleViewManager<View> {
    private static final String TAG = "LiveryViewManager";

    public static final String REACT_CLASS = "LiveryReactNativeView";

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public View createViewInstance(@NonNull ThemedReactContext reactContext) {
        Log.d(TAG, "create LiveryPlayerView instance");
        LiveryPlayerView player = new LiveryPlayerView(reactContext);
        player.registerListener(new LiveryPlayerEventsForwarder(reactContext, player));
        player.setInteractiveBridgeCustomCommandListener(new LiveryInteractiveBridgeForwarder(reactContext, player));
        connectPlayerLifecycle(player, reactContext);
        return player;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        HashMap<String, Object> map = new HashMap<>();
        addEventType(map, "onPlaybackStateDidChange");
        addEventType(map, "onActiveQualityDidChange");
        addEventType(map, "onPlayerError");
        addEventType(map, "onPlayerDidRecover");
        addEventType(map, "onProgressDidChange");
        addEventType(map, "onQualitiesDidChange");
        addEventType(map, "onTimeDidUpdate");
        addEventType(map, "onSourceDidChange");
        addEventType(map, "onVolumeDidChange");
        addEventType(map, "onPlaybackRateDidChanged");

        addEventType(map, "onGetCustomMessageValue");
        return map;
    }

    private void addEventType(HashMap<String, Object> map, String event) {
        map.put(
                event,
                MapBuilder.of(
                        "phasedRegistrationNames",
                        MapBuilder.of("bubbled", event)
                )
        );
    }

    @ReactProp(name = "streamId")
    public void setStreamId(LiveryPlayerView view, String streamId) {
        if (TextUtils.isEmpty(streamId)) return;
        LiverySDK.getInstance().initialize(streamId, new LiverySDK.StateListener() {
            @Override
            public void stateChanged(LiverySDK.State state) {
                if (LiverySDK.State.INITIALIZED.equals(state)) {
                    createPlayer(view);
                } else {
                    Log.e(TAG, "LiverySDK initialize result: " + state.toString() + " error: " + state.error);
                }
            }
        });
    }

    @ReactProp(name = "playbackControlState")
    public void setPlaybackControlState(LiveryPlayerView view, int state) {
        Log.d(TAG, "state: " + state);
        if (!view.isPlayerInitialized()) {
            Log.e(TAG, "player not initialized");
            return;
        }

        if (state == 1) {
            view.play();
        } else {
            view.pause();
        }
    }

    @ReactProp(name = "interactiveURL")
    public void setInteractiveURL(LiveryPlayerView view, String interactiveURL) {
        view.setInteractiveUrl(interactiveURL);
    }

    private void createPlayer(LiveryPlayerView playerView) {
        playerView.createPlayer(new LiveryPlayerView.CreatePlayerListener() {
            @Override
            public void finished() {
                Log.d(TAG, "player created");

                playerView.measure(
                        View.MeasureSpec.makeMeasureSpec(playerView.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(playerView.getMeasuredHeight(), View.MeasureSpec.EXACTLY)
                );
                playerView.layout(playerView.getLeft(), playerView.getTop(), playerView.getRight(), playerView.getBottom());
            }
        }, new LiveryPlayerView.CreatePlayerErrorListener() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error creating player: " + e);
            }
        });
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
