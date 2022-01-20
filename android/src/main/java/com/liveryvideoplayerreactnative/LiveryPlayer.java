package com.liveryvideoplayerreactnative;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LiveryPlayer extends ReactContextBaseJavaModule {

  public static final String REACT_CLASS = "LiveryPlayer";

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  LiveryPlayer(ReactApplicationContext context) {
    super(context);
  }

  @ReactMethod
  public void play() {
  }

  @ReactMethod
  public void pause() {
  }

  @ReactMethod
  public void setInteractiveURL(String urlString) {
  }

  @ReactMethod
  public void sendResponseToInteractiveBridge(String forName, @Nullable String value) {
  }

  @ReactMethod
  public void sendInteractiveBridgeCustomCommand(String forName, @Nullable String value, Callback callback) {
    callback.invoke("NOT IMPLEMENTED");
  }
}
