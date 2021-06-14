import Livery

/// Utility functions for accessing our global Livery SDK instance.
@objc public class LiverySDKProvider: NSObject {
    
    /// The global livery SDK instance.
    private static let livery: LiverySDK = LiverySDK()
    
    private static var isInitialized: Bool = false
    
    /// A queue on which all calls to the Livery SDK should be made.
    public static let queue = DispatchQueue(label: "LiverySDKProvider")
    
    /// The Livery SDK streamId
    @objc public static var streamId: String?
    
    private override init() { }
    
    /// Initialize the Livery SDK if needed, and return the instance.
    /// Must be called on the `LiverySDKProvider` queue.
    public static func initializeSDK(_ completion: @escaping (LiverySDK?) -> Void) {
        guard let streamId = self.streamId else {
            print("Livery SDK initialization failed: streamId is required")
            return completion(nil)
        }
        
        guard !isInitialized else { return completion(livery) }
        
        livery.initialize(streamId: streamId) { result in
            switch result {
            case .success:
                isInitialized = true
                print("Livery SDK initialization was successful")
                completion(livery)
            case .failure(let error):
                isInitialized = false
                print("Livery SDK initialization failed: \(error.localizedDescription)")
                completion(nil)
            }
        }
    }
}
