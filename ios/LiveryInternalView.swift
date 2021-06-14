import UIKit
import Livery

/// Delegate protocol for `LiveryInternalView`.
public protocol LiveryInternalViewDelegate: NSObjectProtocol {
    func liveryView(_ view: LiveryInternalView, didCreatePlayer: Player)
    func liveryView(_ view: LiveryInternalView, willDisposePlayer: Player)
}

extension LiveryInternalViewDelegate {
    public func liveryView(_ view: LiveryInternalView, didCreatePlayer: Player) {}
    public func liveryView(_ view: LiveryInternalView, willDisposePlayer: Player) {}
}

/// A view that wraps and configures a Livery player.
/// This is the shared base used for the actual React Native component views.
open class LiveryInternalView: UIView {
    
    weak open var delegate: LiveryInternalViewDelegate?
    
    /// The player instance for this view. Only accessed from the main thread.
    open private(set) var player: Player?
    
    /// Counter for unique request IDs. Accessed from multiple threads.
    private var requestCounter: Int32 = 0
    
    /// Create a player instance.
    open func createPlayer(options: playerOptions = playerOptions()) {
        assert(player == nil)
        assert(window != nil)
        
        // We're about to perform some async requests. Create a unique request ID
        // and capture it in closures, so we can compare at each step and see if
        // this request is still actual.
        let request = OSAtomicIncrement32(&requestCounter)
        
        // We're careful here to never keep a strong reference to `self` for an
        // extended time. This allows us to check between each step to see if the
        // view has been disposed of in the mean time.
        LiverySDKProvider.queue.async { [weak self] in
            // Initialize the Livery SDK.
            LiverySDKProvider.initializeSDK { (livery) in
                guard let livery = livery else { return }
                
                // Check if our request is still actual.
                guard self?.requestCounter == request else { return }
                
                // Create the Livery player. This may block for a while.
                guard let player = livery.createPlayer(options: options) else {
                    print("Could not create Livery player instance")
                    return
                }
                
                // Finish setup on the main thread.
                DispatchQueue.main.async {
                    guard let self = self, self.requestCounter == request else {
                        // We were interrupted, throw away the player.
                        LiverySDKProvider.queue.async {
                            player.stop()
                            player.dispose()
                        }
                        return
                    }
                    
                    // This is the only call we do off the LiverySDKProvider queue.
                    // @todo: This stalls UI for a bit. Not sure if we can prevent this?
                    player.setView(view: self)
                    self.player = player
                    self.delegate?.liveryView(self, didCreatePlayer: player)
                }
            }
        }
    }
    
    /// Stop and dispose of the player.
    open func disposePlayer() {
        // Increment the request counter to interrupt any in-progress setup.
        OSAtomicIncrement32(&requestCounter)
        
        // Sync with the queue, and dispose of the player.
        // @todo: This stalls UI for a bit. Not sure if we can prevent this?
        LiverySDKProvider.queue.sync {
            if let player = player {
                self.delegate?.liveryView(self, willDisposePlayer: player)
                player.stop()
                player.dispose()
                self.player = nil
            }
        }
    }
    
    /// Destroy the player instance when this view is removed from its window.
    override open func willMove(toWindow newWindow: UIWindow?) {
        disposePlayer()
    }
}
