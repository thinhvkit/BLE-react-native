//
//  ActionIntent.swift
//  iosApp
//
//  Created by thinh.vo on 21/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import AppIntents
import SwiftUI
import shared

struct ActionIntent: AppIntent {
    static let title: LocalizedStringResource = LocalizedStringResource("Red Alert")
    
    static var openAppWhenRun: Bool = true
    
    func perform() throws -> some IntentResult {
        
        guard let token = UserDefaults.standard.string(forKey: DataStoreKeys.shared.TOKEN), !token.isEmpty else {
            Logger.shared.d(message: "Token is invalid", tag: "")
            return .result()
        }
        
        if let shortcut = URL(string: "myprotect://red-alert"){
            EnvironmentValues().openURL(shortcut)
        }
        
        return .result()
    }
}

extension String {
    func convertToDictionary() -> [String: Any]? {
        if let data = data(using: .utf8) {
            return try? JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
        }
        return nil
    }
}
