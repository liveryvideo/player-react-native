package com.liveryvideoplayerreactnative;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.common.MapBuilder;

import java.util.Map;

enum CommandsMap {
    SET_INTERACTIVE_URL("setInteractiveURL"),
    SEND_INTERACTIVE_BRIDGE_CUSTOM_COMMAND("sendInteractiveBridgeCustomCommand"),
    SEND_RESPONSE_TO_INTERACTIVE_BRIDGE("sendResponseToInteractiveBridge"),
    UNKNOWN(null)
    ;

    @Nullable
    final String name;
    final int index;

    @SuppressWarnings("UnnecessaryEnumModifier")
    private CommandsMap(@Nullable String name) {
        this.name = name;
        this.index = ordinal();
    }

    @NonNull
    static CommandsMap from(int id) {
        for (CommandsMap v : values()) {
            if (v.index == id) return v;
        }
        return UNKNOWN;
    }

    @NonNull
    static CommandsMap fromStringId(String id) {
        try {
            return from(Integer.parseInt(id));
        } catch (Exception ignore) { }
        return UNKNOWN;
    }

    static Map<String, Integer> buildMap() {
        MapBuilder.Builder<String, Integer> builder = MapBuilder.builder();
        for (CommandsMap v : values()) {
            if (TextUtils.isEmpty(v.name)) continue;
            builder.put(v.name, v.index);
        }
        return builder.build();
    }
}
