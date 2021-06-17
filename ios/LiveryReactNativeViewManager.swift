import Livery

@objc(LiveryReactNativeViewManager)
class LiveryReactNativeViewManager: RCTViewManager {
    
    override func view() -> (LiveryReactNativeView) {
        return LiveryReactNativeView()
    }
}

class LiveryReactNativeView: UIView {
    
    private var livery: LiverySDK = LiverySDK()
    private var player: Player?
        
    @objc open var streamId: String? {
        didSet {
            guard let streamId = self.streamId else { return }
            initializeSDK(streamId: streamId)
        }
    }
    
    /// Called when the playback state change
    @objc open var onPlaybackStateDidChange: RCTBubblingEventBlock?
    
    private func initializeSDK(streamId: String) {
        player?.stop()
        
        livery.initialize(streamId: streamId) { [weak self] result in
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
        guard let player = livery.createPlayer() else {
            print("Could not create Livery player instance")
            return
        }
        
        player.setView(view: self)
        player.delegate = self
        self.player = player
    }
}

extension LiveryReactNativeView: PlayerDelegate {
    func playbackStateDidChange(playbackState: Player.PlaybackState) {
        print("playbackStateDidChange state: \(playbackState.description)")
        onPlaybackStateDidChange?(["state": playbackState.description])
    }
}
