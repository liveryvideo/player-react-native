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
        event("onActiveQualityDidChange")
                .putString("activeQuality", quality.label)
                .putBoolean("auto", auto)
                .emit();
    }

    @Override
    public void onPlayerError(Exception e) {
        event("onPlayerError")
                .putString("error", e.getLocalizedMessage())
                .emit();
    }

    @Override
    public void onPlayerStateChanged(LiveryPlayerState playerState) {
        event("onPlaybackStateDidChange")
                .putString("playerState", playerState.toString())
                .emit();
    }

    @Override
    public void onProgressChanged(long buffer, long latency) {
        event("onProgressDidChange")
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

        event("onQualitiesDidChange")
                .putString("qualities", qualitiesLabelString)
                .emit();
    }

    @Override
    public void onPlaybackRateChanged(float rate) {
        event("onPlaybackRateDidChanged")
                .putDouble("rate", rate)
                .emit();
    }

    @Override
    public void onRecovered() {
        event("onPlayerDidRecover")
                .emit();
    }

    @Override
    public void onTimeUpdate(long currentTime) {
        event("onTimeDidUpdate")
                .putInt("currentTime", (int) currentTime)
                .emit();
    }

    @Override
    public void onVolumeChanged(float volume) {
        event("onVolumeDidChange")
                .putDouble("volume", volume)
                .emit();
    }

    @Override
    public void onSourceChanged(String source) {
        event("onSourceDidChange")
                .putString("source", source)
                .emit();
    }
}
