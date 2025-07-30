//
//  Incapacitation.swift
//  iosApp
//
//  Created by thinh.vo on 25/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ActivityKit
import UIKit
import shared

class Incapacitation : ObservableObject {
    @Published private(set) var token: String?
    @Published private(set) var stateUpdate: ActivityContent<IncapacitationAttributes.ContentState>?
    
    
    static let shared = Incapacitation()
    
    private var currentActivity: Activity<IncapacitationAttributes>?
    private var duration: TimeInterval? = 0
    
    private func areActivitiesEnabled() -> Bool {
        return ActivityAuthorizationInfo().areActivitiesEnabled
    }
    
    private func resetValues() {
        duration = nil
        currentActivity = nil
    }
    
    func stop() -> Void {
        IosIncapacitationManager.Companion.shared.stop()
    }
    
    func increase() -> Void {
        IosIncapacitationManager.Companion.shared.increase()
    }
    
    
    @available(iOS 16.2, *)
    func startLiveActivity(_ timestamp: Double) -> Void {
        if(currentActivity != nil){
            return
        }
        duration = timestamp
        if (!areActivitiesEnabled()) {
            // User disabled Live Activities for the app, nothing to do
            return
        }
        let activityAttributes = IncapacitationAttributes()
        let contentState = IncapacitationAttributes.ContentState(duration: duration)
        let activityContent = ActivityContent(state: contentState,  staleDate: nil)
        do {
            // Request to start a new Live Activity with the content defined above
            let activity = try Activity.request(attributes: activityAttributes, content: activityContent)
            currentActivity = activity
            registerToken(activity: activity)

        } catch {
            // Handle errors, skipped for simplicity
        }
    }
    
    func endActivity(dismissTimeInterval: Double?) async {
        guard let activity = currentActivity else {
            return
        }
        
        let finalContent = IncapacitationAttributes.ContentState(duration: duration)
        
        let dismissalPolicy: ActivityUIDismissalPolicy
        if let dismissTimeInterval = dismissTimeInterval {
            if dismissTimeInterval <= 0 {
                dismissalPolicy = .immediate
            } else {
                dismissalPolicy = .after(.now + dismissTimeInterval)
            }
        } else {
            dismissalPolicy = .default
        }
        
        await activity.end(ActivityContent(state: finalContent, staleDate: nil), dismissalPolicy: dismissalPolicy)
    }
    
    func update(_ timestamp: String) -> Void {
    
        Logger.shared.d(message: timestamp, tag: "")
        
        Task {
            duration = timestamp.convertToTimeInterval()
            let contentState = IncapacitationAttributes.ContentState(duration: duration)
            
            await currentActivity?.update(ActivityContent<IncapacitationAttributes.ContentState>(
                state: contentState,
                staleDate: nil
            ))
        }
    }
    
    func update(_ timestamp: Double) -> Void {
        
        duration = timestamp
        
        Task {
            
            let contentState = IncapacitationAttributes.ContentState(duration: duration)
            
            await currentActivity?.update(ActivityContent<IncapacitationAttributes.ContentState>(
                state: contentState,
                staleDate: nil
            ))
        }
    }
    
    func stopLiveActivity() -> Void {
        duration = nil
        currentActivity = nil
       
        Task {
            for activity in Activity<IncapacitationAttributes>.activities {
                await activity.end(nil, dismissalPolicy: .immediate)
            }
        }
    }
    
    func registerToken(
        activity: Activity<IncapacitationAttributes>
    ) {
        Task {
            for await data in activity.pushTokenUpdates {
                let token = data.map { String(format: "%02x", $0) }.joined()
                Logger.shared.d(message: "Activity token: \(token)", tag: "")
                self.token = token
            }
        }
        
        Task {
            for await stateUpdate in activity.contentUpdates {
                Logger.shared.d(message: "Activity update: \(stateUpdate)", tag: "")
                self.stateUpdate = stateUpdate
            }
        }
    }
    
    func redAlert(number: String){
        Logger.shared.d(message: "Red alert calling", tag: "")
    }
}

extension String {
    
    // MARK: Get remove all characters exept numbers
    
    func onlyDigits() -> String {
        let filtredUnicodeScalars = unicodeScalars.filter { CharacterSet.decimalDigits.contains($0) }
        return String(String.UnicodeScalarView(filtredUnicodeScalars))
    }
    
    func convertToTimeInterval() -> TimeInterval {
            guard self != "" else {
                return 0
            }

            var interval:Double = 0

            let parts = self.components(separatedBy: ":")
            for (index, part) in parts.reversed().enumerated() {
                interval += (Double(part) ?? 0) * pow(Double(60), Double(index))
            }

            return interval
        }
}
