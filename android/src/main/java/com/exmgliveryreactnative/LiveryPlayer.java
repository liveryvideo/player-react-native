package com.exmgliveryreactnative;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiverySDK;
import tv.exmg.livery.interactivebridge.LiveryInteractiveBridge;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class LiveryPlayer extends ReactContextBaseJavaModule {

  public static final String REACT_CLASS = "LiveryPlayer";

  private static LiveryPlayerView playerView;
  private static LiverySDK liverySDK;
  private static Map<String, LiveryInteractiveBridge.CustomCommandResultCallback> interactiveBridgeMessages = new HashMap<String, LiveryInteractiveBridge.CustomCommandResultCallback>();

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  LiveryPlayer(ReactApplicationContext context) {
    super(context);
  }

  public LiveryPlayer(LiveryPlayerView playerView) {
    LiveryPlayer.playerView = playerView;
    LiveryPlayer.liverySDK = LiverySDK.getInstance();
  }

  @ReactMethod
  public void play() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        playerView.play();
      }
    });
  }

  @ReactMethod
  public void pause() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        playerView.pause();
      }
    });
  }

  @ReactMethod
  public void setInteractiveURL(String urlString) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        playerView.setInteractiveUrl(urlString);
      }
    });
  }

  @ReactMethod
  public void sendResponseToInteractiveBridge(String forName, @Nullable String value) {
    Log.d("[responseToIntBridge]","name: [" + forName + "] value: [" + value + "]");
    interactiveBridgeMessages.get(forName).result(value);
    interactiveBridgeMessages.remove(forName);
  }

  @ReactMethod
  public void sendInteractiveBridgeCustomCommand(String forName, @Nullable String value, Callback callback) {
    Log.d("[customCmdToIntBridge]","name: [" + forName + "] value: [" + value + "]");
    playerView.sendInteractiveBridgeCustomCommand(forName, value, new LiveryInteractiveBridge.CustomCommandResponseCallback() {
      @Override
      public void result(@Nullable Object response, @Nullable String error) {
        if (response != null) {
          callback.invoke(response);
        } else {
          callback.invoke(error);
        }
      }
    });
  }

  public void setStreamId(String streamId) {
    liverySDK.initialize(streamId, new LiverySDK.StateListener() {
      @Override
      public void stateChanged(LiverySDK.State state) {
        Log.d("CONSOLE LOG: ","STATE CHANGED " + state.toString());
        if (LiverySDK.State.INITIALIZED.equals(state)) {
          createPlayer();
        }
      }
    });
  }

  public void addMessage(String name, @Nullable LiveryInteractiveBridge.CustomCommandResultCallback customCommandResultCallback) {
    interactiveBridgeMessages.put(name, customCommandResultCallback);
  }

  private void createPlayer() {
    playerView.createPlayer(new LiveryPlayerView.CreatePlayerListener() {
      @Override
      public void finished() {
        Log.d("[LiveryPlayer]", "create player finished...");

        playerView.measure(
          View.MeasureSpec.makeMeasureSpec(playerView.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(playerView.getMeasuredHeight(), View.MeasureSpec.EXACTLY)
        );
        playerView.layout(playerView.getLeft(), playerView.getTop(), playerView.getRight(), playerView.getBottom());

      }
    }, new LiveryPlayerView.CreatePlayerErrorListener() {
      @Override
      public void onError(Exception e) {
        Log.d("[LiveryPlayer]", "create player error: " + e);
      }
    });
  }
}
