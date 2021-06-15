import Livery

@objc(LiveryReactNativeViewManager)
class LiveryReactNativeViewManager: RCTViewManager {
    
    override func view() -> (LiveryReactNativeView) {
        return LiveryReactNativeView()
    }
}

class LiveryReactNativeView : UIView {
    
    private var isInitialized: Bool = false
    private var livery: LiverySDK = LiverySDK()
    private var player: Player?
        
    @objc var streamId: String? {
        didSet {
            guard let streamId = self.streamId else { return }
            initializeSDK(streamId: streamId)
        }
    }
    
    
    private func initializeSDK(streamId: String) {
        player?.stop()
        
        livery.initialize(streamId: streamId) { [weak self] result in
            guard let self = self else { return }
            
            switch result {
            case .success:
                self.isInitialized = true
                print("Livery SDK initialization was successful")
                DispatchQueue.main.async {
                    self.createPlayer()
                }
                
            case .failure(let error):
                self.isInitialized = false
                print("Livery SDK initialization failed: \(error.localizedDescription)")
            }
        }
    }
    
    private func createPlayer() {
        // Create the Livery player. This may block for a while.
        guard let player = livery.createPlayer() else {
            print("Could not create Livery player instance")
            return
        }
        
        DispatchQueue.main.async {
            player.setView(view: self)
            self.player = player
        }
    }
}
