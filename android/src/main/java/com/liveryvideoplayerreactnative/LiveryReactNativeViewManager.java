package com.liveryvideoplayerreactnative;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiverySDK;
import tv.exmg.livery.interactivebridge.LiveryInteractiveBridge;

@SuppressWarnings({"unused", "Convert2Lambda"})
public class LiveryReactNativeViewManager extends SimpleViewManager<View> {
    private static final String TAG = "LiveryViewManager";

    public static final String REACT_CLASS = "LiveryReactNativeView";

    class LiveryPlayerViewWrapper {
        final LiveryPlayerView playerView;
        final LiveryInteractiveBridgeForwarder interactiveBridgeForwarder;

        LiveryPlayerViewWrapper(LiveryPlayerView playerView, LiveryInteractiveBridgeForwarder interactiveBridgeForwarder) {
            this.playerView = playerView;
            this.interactiveBridgeForwarder = interactiveBridgeForwarder;
        }
    }
    private final HashSet<LiveryPlayerViewWrapper> playersData = new HashSet<>();

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
        LiveryInteractiveBridgeForwarder interactiveBridgeForwarder = new LiveryInteractiveBridgeForwarder(reactContext, player);
        player.setInteractiveBridgeCustomCommandListener(interactiveBridgeForwarder);
        connectPlayerLifecycle(player, reactContext);
        playersData.add(new LiveryPlayerViewWrapper(player, interactiveBridgeForwarder));
        return player;
    }

    @Override
    public void onDropViewInstance(@NonNull View view) {
        if (!(view instanceof LiveryPlayerView)) return;
        LiveryPlayerView player = (LiveryPlayerView) view;
        player.registerListener(null);
        player.setInteractiveBridgeCustomCommandListener(null);
        // ToDo: undo what connectPlayerLifecycle does

        for (LiveryPlayerViewWrapper data : playersData) {
            if (data.playerView == player) {
                data.interactiveBridgeForwarder.interactiveBridgeMessages.clear();
                playersData.remove(data);
                break;
            }
        }
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        HashMap<String, Object> map = new HashMap<>();
        for (CallbackEvents event: CallbackEvents.values()) {
            addEventType(map, event.name);
        }
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

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return CommandsMap.buildMap();
    }

    @Override
    public void receiveCommand(@NonNull View root, int commandId, @Nullable ReadableArray args) {
        receiveCommand(root, Integer.toString(commandId), args);
    }

    @Override
    public void receiveCommand(@NonNull View root, String commandId, @Nullable ReadableArray args) {
        if (!(root instanceof LiveryPlayerView)) return;
        LiveryPlayerView playerView = (LiveryPlayerView) root;

        CommandsMap command = CommandsMap.fromStringId(commandId);
        switch (command) {
            case SET_INTERACTIVE_URL: {
                String url = args != null ? args.getString(0) : null;
                playerView.setInteractiveUrl(url);

                break;
            }
            case SEND_INTERACTIVE_BRIDGE_CUSTOM_COMMAND: {
                String name = args != null ? args.getString(0) : null;
                String arg = args != null ? args.getString(1) : null;
                sendInteractiveBridgeCustomCommand(playerView, name, arg);

                break;
            }
            case SEND_RESPONSE_TO_INTERACTIVE_BRIDGE: {
                String name = args != null ? args.getString(0) : null;
                String value = args != null ? args.getString(1) : null;
                sendResponseToInteractiveBridge(playerView, name, value);

                break;
            }
            default:
                Log.d(TAG, "unhandled command " + commandId + " with " + args + " on " + root);
        }

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

    private void sendInteractiveBridgeCustomCommand(LiveryPlayerView playerView, String name, String arg) {
        playerView.sendInteractiveBridgeCustomCommand(name, arg, new LiveryInteractiveBridge.CustomCommandResponseCallback() {
            @Override
            public void result(@Nullable Object response, @Nullable String error) {
                Log.d(TAG, "result of " + name + " value: " + response + " error: " + error);
                Forwarder forwarder = new Forwarder("CustomCommandResult",
                        (ReactContext) playerView.getContext(),
                        playerView);
                forwarder.event(CallbackEvents.ON_INTERACTIVE_BRIDGE_CUSTOM_COMMAND_RESPONSE)
                        .putString("name", name)
                        .putString("arg", arg)
                        .putObject("response", response)
                        .putString("error", error)
                        .emit();
            }
        });
    }

    private void sendResponseToInteractiveBridge(LiveryPlayerView playerView, String name, String value) {
        for (LiveryPlayerViewWrapper data : playersData) {
            if (data.playerView == playerView) {
                data.interactiveBridgeForwarder.sendResponse(name, value);
                break;
            }
        }
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
