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
    /// Called when the active quality change
    @objc var onActiveQualityDidChange: RCTBubblingEventBlock?
    /// Called when there's an error on the player
    @objc var onPlayerError: RCTBubblingEventBlock?
    /// Called when the player recover from an error
    @objc var onPlayerDidRecover: RCTBubblingEventBlock?
    /// Called when the player progress change
    @objc var onProgressDidChange: RCTBubblingEventBlock?
    /// Called when the player qualities change
    @objc var onQualitiesDidChange: RCTBubblingEventBlock?
    /// Called when the player selected quality change
    @objc var onSelectedQualityDidChange: RCTBubblingEventBlock?
    /// Called when the player current source change
    @objc var onSourceDidChange: RCTBubblingEventBlock?
    /// Called when the player current time change
    @objc var onTimeDidUpdate: RCTBubblingEventBlock?
    /// Called when the player gets a custom message from the interactive bridge
    @objc var onGetCustomMessageValue: RCTBubblingEventBlock?
}

// MARK: Player Delegate
extension LiveryReactNativeView: PlayerDelegate {
    func playbackStateDidChange(playbackState: Player.PlaybackState) {
        onPlaybackStateDidChange?(["playbackState": playbackState.description])
    }
    
    func activeQualityDidChange(activeQuality: Quality?) {
        onActiveQualityDidChange?(["activeQuality": activeQuality?.label ?? NSNull()])
    }
    
    func playerDidFail(error: Error) {
        onPlayerError?(["error": error.localizedDescription])
    }
    
    func playerDidRecover() {
        onPlayerDidRecover?([:])
    }
    
    func progressDidChange(buffer: Int, latency: Int) {
        onProgressDidChange?(["buffer": buffer, "latency": latency])
    }
    
    func qualitiesDidChange(qualities: [Quality]) {
        let qualitiesLabel = qualities.compactMap({ $0.label })
        onQualitiesDidChange?(["qualities": qualitiesLabel])
    }
    
    func selectedQualityDidChange(selectedQuality: Quality?) {
        onSelectedQualityDidChange?(["selectedQuality": selectedQuality?.label ?? NSNull()])
    }
    
    func sourceDidChange(currentSource: URL?) {
        onSourceDidChange?(["currentSource": currentSource?.absoluteString ?? NSNull()])
    }
    
    func timeDidUpdate(currentTime: TimeInterval) {
        onTimeDidUpdate?(["currentTime": currentTime])
    }
}

extension LiveryReactNativeView: PlayerInteractiveBridgeDelegate {
    func getCustomMessageValue(message name: String, arg: Any?, completionHandler: @escaping (Any?) -> Void) {
        print("[Player Interactive Bridge] getCustomMessageValue name: [\(name)] arg: [\(arg ?? "nil")]")
        
        player.addMessage(with: name, handler: completionHandler)
        onGetCustomMessageValue?(["name": name, "arg": arg ?? NSNull()])
    }
}
