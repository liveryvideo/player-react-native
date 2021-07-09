//
//  LiveryPlayer.swift
//  exmg-livery-react-native
//
//  Created by Jose Nogueira on 05/07/2021.
//

import Foundation
import Livery

typealias PlayerDelegateInteractive = PlayerDelegate & PlayerInteractiveBridgeDelegate

@objc(LiveryPlayer) class LiveryPlayer: NSObject {
    
    private static var livery: LiverySDK = LiverySDK()
    private static var player: Player?
    
    private static var interactiveBridgeMessages: [String: (Any?) -> Void] = [:]
    
    @objc func play() {
        LiveryPlayer.player?.play()
    }
    
    @objc func pause() {
        LiveryPlayer.player?.pause()
    }
    
    @objc func setInteractiveURL(_ interactiveURL: String) {
        LiveryPlayer.player?.interactiveURL = URL(string: interactiveURL)
    }
    
    @objc static func requiresMainQueueSetup() -> Bool {
        return true
    }
}

// MARK: Initialize SDK
extension LiveryPlayer {
    func initializeSDK(streamId: String, on view: UIView, delegate: PlayerDelegateInteractive) {
        LiveryPlayer.player?.stop()
        
        LiveryPlayer.livery.initialize(streamId: streamId) { [weak self] result in
            guard let self = self else { return }
            
            switch result {
            case .success:
                print("Livery SDK initialization was successful")
                DispatchQueue.main.async {
                    self.create(on: view, delegate: delegate)
                }
                
            case .failure(let error):
                print("Livery SDK initialization failed: \(error.localizedDescription)")
            }
        }
    }
}

// MARK: Create Player
extension LiveryPlayer {
    private func create(on view: UIView, delegate: PlayerDelegateInteractive) {
        guard let player = LiveryPlayer.livery.createPlayer() else {
            print("Could not create Livery player instance")
            return
        }
        
        player.setView(view: view)
        player.delegate = delegate
        player.interactiveBridgeDelegate = delegate
        LiveryPlayer.player = player
    }
}

// MARK: Player Interactive Bridge
extension LiveryPlayer {
    @objc func sendInteractiveBridgeCustomCommand(_ name: String, arg: Any?, callback: @escaping RCTResponseSenderBlock) {
        print("[Player Interactive Bridge] sendInteractiveBridgeCustomCommand")
        
        LiveryPlayer.player?.sendInteractiveBridgeCustomCommand(name: name, arg: arg, completionHandler: { result in
            switch result {
            case .success(let value):
                print("[Player Interactive Bridge] name: [\(name)] arg: [\(arg ?? "nil")] response value: [\(value ?? "nil")]")
                callback([NSNull(), value ?? NSNull()])
            case .failure(let error):
                print("[Player Interactive Bridge] name: [\(name)] arg: [\(arg ?? "nil")] error: [\(error)]")
                callback([error, NSNull()])
            @unknown default:
                break
            }
        })
    }
    
    func addMessage(with name: String, handler: @escaping (Any?) -> Void) {
        LiveryPlayer.interactiveBridgeMessages[name] = handler
    }
    
    @objc func sendResponseToInteractiveBridge(_ forName: String, value: Any?) {
        print("[Player Interactive Bridge] sendResponseToInteractiveBridge name: [\(forName)] | value: [\(value ?? "nil")]")
        LiveryPlayer.interactiveBridgeMessages[forName]?(value)
        LiveryPlayer.interactiveBridgeMessages.removeValue(forKey: forName)
    }
}
