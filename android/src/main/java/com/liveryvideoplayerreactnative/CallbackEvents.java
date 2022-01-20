package com.liveryvideoplayerreactnative;

enum CallbackEvents {
    ON_PLAYBACK_STATE_DID_CHANGE("onPlaybackStateDidChange"),
    ON_ACTIVE_QUALITY_DID_CHANGE("onActiveQualityDidChange"),
    ON_PLAYER_ERROR("onPlayerError"),
    ON_PLAYER_DID_RECOVER("onPlayerDidRecover"),
    ON_PROGRESS_DID_CHANGE("onProgressDidChange"),
    ON_QUALITIES_DID_CHANGE("onQualitiesDidChange"),
    ON_TIME_DID_UPDATE("onTimeDidUpdate"),
    ON_SOURCE_DID_CHANGE("onSourceDidChange"),
    ON_VOLUME_DID_CHANGE("onVolumeDidChange"),
    ON_PLAYBACK_RATE_DID_CHANGED("onPlaybackRateDidChanged"),
    ON_GET_CUSTOM_MESSAGE_VALUE("onGetCustomMessageValue"),
    ON_INTERACTIVE_BRIDGE_CUSTOM_COMMAND_RESPONSE("onInteractiveBridgeCustomCommandResponse"),
    ;
    final String name;

    private CallbackEvents(String name) {
        this.name = name;
    }
}
