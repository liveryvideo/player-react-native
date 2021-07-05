import Livery

@objc(LiveryReactNativeViewManager)
class LiveryReactNativeViewManager: RCTViewManager {
    
    override func view() -> (LiveryReactNativeView) {
        return LiveryReactNativeView()
    }
}

class LiveryReactNativeView: UIView {
    
    // MARK: Properties
    private var player: LiveryPlayer = LiveryPlayer()
    
    @objc var streamId: String? {
        didSet {
            guard let streamId = self.streamId else { return }
            player.initializeSDK(streamId: streamId, on: self, delegate: self)
        }
    }
    
    /// Called when the playback state change
    @objc var onPlaybackStateDidChange: RCTBubblingEventBlock?
    
    /// Called when the player gets a custom message from the interactive bridge
    @objc var onGetCustomMessageValue: RCTBubblingEventBlock?
}

// MARK: Player Delegate
extension LiveryReactNativeView: PlayerDelegate {
    func playbackStateDidChange(playbackState: Player.PlaybackState) {
        onPlaybackStateDidChange?(["playbackState": playbackState.description])
    }
}

extension LiveryReactNativeView: PlayerInteractiveBridgeDelegate {
    func getCustomMessageValue(message name: String, arg: Any?, completionHandler: @escaping (Any?) -> Void) {
        print("[Player Interactive Bridge] getCustomMessageValue name: [\(name)] arg: [\(arg ?? "nil")]")
        
        player.addMessage(with: name, handler: completionHandler)
        onGetCustomMessageValue?(["name": name, "arg": arg ?? NSNull()])
    }
}
