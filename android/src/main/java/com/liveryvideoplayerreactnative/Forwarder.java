package com.liveryvideoplayerreactnative;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.ref.WeakReference;

import tv.exmg.livery.LiveryPlayerView;

class Forwarder {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG;
    private final WeakReference<LiveryPlayerView> weakPlayerView;
    private final WeakReference<ReactContext> weakContext;

    Forwarder(String TAG, ReactContext context, LiveryPlayerView playerView) {
        this.TAG = TAG;
        weakContext = new WeakReference<>(context);
        weakPlayerView = new WeakReference<>(playerView);
    }


    protected Event event(String name) {
        return new Event(name);
    }

    @SuppressWarnings("SameParameterValue")
    protected class Event {
        final String name;
        final private WritableMap data = Arguments.createMap();

        Event(String name) {
            this.name = name;
        }

        Event putString(String key, String value) {
            data.putString(key, value);
            return this;
        }

        Event putBoolean(String key, boolean value) {
            data.putBoolean(key, value);
            return this;
        }

        Event putInt(String key, int value) {
            data.putInt(key, value);
            return this;
        }

        Event putDouble(String key, double value) {
            data.putDouble(key, value);
            return this;
        }

        void emit() {
            ReactContext context = weakContext.get();
            if (context == null) {
                Log.e(TAG, "context is null");
                return;
            }
            LiveryPlayerView playerView = weakPlayerView.get();
            if (playerView == null) {
                Log.e(TAG, "playerView is null");
                return;
            }
            int viewId = playerView.getId();

            Log.d(TAG, "view [" + viewId + "] event: " + name + " " + data.toString());

            context.getJSModule(RCTEventEmitter.class)
                    .receiveEvent(viewId, name, data);
        }
    }
}
