import UIKit
import Livery
import React

/// The view class backing a `<LiveryPlayer>` component.
@objc open class LiveryPlayerView: LiveryInternalView {
    
    /// Called when the player was successfully created.
    @objc open var onVideoStateChanged: RCTBubblingEventBlock?
    
    /// Create a player instance when this view is added to the hierarchy.
    override open func didMoveToWindow() {
        guard window != nil else { return }
        createPlayer()
    }
}

extension LiveryPlayerView: LiveryInternalViewDelegate {
    open func liveryView(_ view: LiveryInternalView, didCreatePlayer: Player) {
        // TODO: Implement PlayerDelegate to get the state of the player
        self.onVideoStateChanged?(["state": "ok"])
    }
}
