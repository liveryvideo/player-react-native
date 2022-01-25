import Livery

@objc(LiveryReactNativeViewManager)
class LiveryReactNativeViewManager: RCTViewManager {
    
    override func view() -> (LiveryReactNativeView) {
        return LiveryReactNativeView()
    }
    
    @objc override static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    private func withView(tag: NSNumber, _ block: @escaping (LiveryReactNativeView) -> Void) {
        bridge.uiManager.addUIBlock { _, viewRegistry in
            guard let viewRegistry = viewRegistry else { return }
            guard let view = viewRegistry[tag] else { return }
            guard let playerView = view as? LiveryReactNativeView else { return }
            block(playerView)
        }
    }
    
    @objc
    func setInteractiveURL(_ reactTag: NSNumber, url: String?) {
        withView(tag: reactTag) { playerView in
            playerView.setInteractiveURL(url)
        }
    }
    
    @objc
    func sendInteractiveBridgeCustomCommand(_ reactTag: NSNumber, name: String, arg: Any?) {
        withView(tag: reactTag) { playerView in
            playerView.sendInteractiveBridgeCustomCommand(name: name, arg: arg)
        }
    }
    
    @objc
    func sendResponseToInteractiveBridge(
        _ reactTag: NSNumber,
        name: String,
        value: Any?
    ) {
        withView(tag: reactTag) { playerView in
            playerView.sendResponseToInteractiveBridge(name: name, value: value)
        }
    }
}

class LiveryReactNativeView: UIView {
    
    // MARK: Properties
    private let sdk: LiverySDK = LiverySDK()
    private var player: Player?
    
    private var interactiveBridgeMessages: [String: (Any?) -> Void] = [:]

    @objc func setStreamId(_ streamId: String?) {
        guard let streamId = streamId else { return }
        initializeSDK(streamId: streamId)
    }

    @objc func setPlaybackControlState(_ state: Int) {
        guard let player = player else { return }
        if state == 1 {
            player.play()
        } else {
            player.pause()
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
    /// Called when the player volume change
    @objc var onVolumeDidChange: RCTBubblingEventBlock?
    /// Called when the player gets a custom message from the interactive bridge
    @objc var onGetCustomMessageValue: RCTBubblingEventBlock?
    
    @objc var onInteractiveBridgeCustomCommandResponse: RCTBubblingEventBlock?
}

// MARK: Create Player
extension LiveryReactNativeView {

    func initializeSDK(streamId: String) {
        player?.stop()

        sdk.initialize(streamId: streamId) { [weak self] result in
            guard let self = self else { return }

            switch result {
            case .success:
                print("Livery SDK initialization was successful")
                DispatchQueue.main.async {
                    self.createPlayer()
                }

            case .failure(let error):
                print("Livery SDK initialization failed: \(error.localizedDescription)")
            }
        }
    }

    private func createPlayer() {
        guard let player = sdk.createPlayer() else {
            print("Could not create Livery player instance")
            return
        }

        player.setView(view: self)
        player.delegate = self
        player.interactiveBridgeDelegate = self
        self.player = player
    }
    
    func setInteractiveURL(_ interactiveURL: String?) {
        guard let interactiveURL = interactiveURL else { return }
        guard let player = player else { return }
        player.interactiveURL = URL(string: interactiveURL)
    }
    
    func sendInteractiveBridgeCustomCommand(name: String, arg: Any?) {
        guard let player = player else { return }
        player.sendInteractiveBridgeCustomCommand(name: name, arg: arg) { [weak self] result in
            var response: [String: Any] = [
                "name": name,
                "arg": arg ?? NSNull(),
                "response": NSNull(),
                "error": NSNull()
            ]
            switch result {
            case .success(let value):
                print("[Player Interactive Bridge] name: [\(name)] arg: [\(arg ?? "nil")] response value: [\(value ?? "nil")]")
                response["response"] = value ?? NSNull()
            case .failure(let error):
                print("[Player Interactive Bridge] name: [\(name)] arg: [\(arg ?? "nil")] error: [\(error)]")
                response["error"] = error
            @unknown default:
                break
            }
            self?.onInteractiveBridgeCustomCommandResponse?(response)
        }
    }
    
    func sendResponseToInteractiveBridge(name: String, value: Any?) {        
        print("[Player Interactive Bridge] sendResponseToInteractiveBridge name: [\(name)] | value: [\(value ?? "nil")]")
        let callback = interactiveBridgeMessages.removeValue(forKey: name)
        callback?(value)
    }

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

    func volumeDidChange() {
        onVolumeDidChange?([:])
    }
}

extension LiveryReactNativeView: PlayerInteractiveBridgeDelegate {
    func getCustomMessageValue(message name: String, arg: Any?, completionHandler: @escaping (Any?) -> Void) {
        print("[Player Interactive Bridge] getCustomMessageValue name: [\(name)] arg: [\(arg ?? "nil")]")

        interactiveBridgeMessages[name] = completionHandler
        onGetCustomMessageValue?(["name": name, "arg": arg ?? NSNull()])
    }
}
