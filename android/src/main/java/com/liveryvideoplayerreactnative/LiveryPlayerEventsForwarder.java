package com.liveryvideoplayerreactnative;

import com.facebook.react.bridge.ReactContext;

import java.util.ArrayList;
import java.util.List;

import tv.exmg.livery.LiveryPlayerListener;
import tv.exmg.livery.LiveryPlayerState;
import tv.exmg.livery.LiveryPlayerView;
import tv.exmg.livery.LiveryQuality;

class LiveryPlayerEventsForwarder extends Forwarder implements LiveryPlayerListener {

    LiveryPlayerEventsForwarder(ReactContext context, LiveryPlayerView playerView) {
        super("LiveryEventsForwarder", context, playerView);
    }

    @Override
    public void onActiveQualityChanged(LiveryQuality quality, boolean auto) {
        event(CallbackEvents.ON_ACTIVE_QUALITY_DID_CHANGE)
                .putString("activeQuality", quality.label)
                .putBoolean("auto", auto)
                .emit();
    }

    @Override
    public void onPlayerError(Exception e) {
        event(CallbackEvents.ON_PLAYER_ERROR)
                .putString("error", e.getLocalizedMessage())
                .emit();
    }

    @Override
    public void onPlayerStateChanged(LiveryPlayerState playerState) {
        event(CallbackEvents.ON_PLAYBACK_STATE_DID_CHANGE)
                .putString("playerState", playerState.toString())
                .emit();
    }

    @Override
    public void onProgressChanged(long buffer, long latency) {
        event(CallbackEvents.ON_PROGRESS_DID_CHANGE)
                .putInt("buffer", (int) buffer)
                .putInt("latency", (int) latency)
                .emit();
    }

    @Override
    public void onQualitiesChanged(List<LiveryQuality> qualities) {
        // ToDo: this is not correct. should be an array
        List<String> qualitiesLabel = new ArrayList<>();
        for (LiveryQuality quality : qualities) {
            qualitiesLabel.add(quality.label);
        }

        String qualitiesLabelString = qualitiesLabel.toString();

        event(CallbackEvents.ON_QUALITIES_DID_CHANGE)
                .putString("qualities", qualitiesLabelString)
                .emit();
    }

    @Override
    public void onPlaybackRateChanged(float rate) {
        event(CallbackEvents.ON_PLAYBACK_RATE_DID_CHANGED)
                .putDouble("rate", rate)
                .emit();
    }

    @Override
    public void onRecovered() {
        event(CallbackEvents.ON_PLAYER_DID_RECOVER)
                .emit();
    }

    @Override
    public void onTimeUpdate(long currentTime) {
        event(CallbackEvents.ON_TIME_DID_UPDATE)
                .putInt("currentTime", (int) currentTime)
                .emit();
    }

    @Override
    public void onVolumeChanged(float volume) {
        event(CallbackEvents.ON_VOLUME_DID_CHANGE)
                .putDouble("volume", volume)
                .emit();
    }

    @Override
    public void onSourceChanged(String source) {
        event(CallbackEvents.ON_SOURCE_DID_CHANGE)
                .putString("source", source)
                .emit();
    }
}
