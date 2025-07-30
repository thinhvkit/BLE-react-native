//
//  AppIntent.swift
//  Incapacitation
//
//  Created by thinh.vo on 26/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import WidgetKit
import AppIntents
import SwiftUI

struct ExtendTimerIntent: LiveActivityIntent {
    
    public init() {}

    
    static var title: LocalizedStringResource = "Extend"
    
    func perform() async throws -> some IntentResult {
        await Incapacitation.shared.increase()
        return .result()
    }
    
}

struct CancelTimerIntent: LiveActivityIntent {
    public init() {}
    
    static var title: LocalizedStringResource = "Cancel"
    
    func perform() async throws -> some IntentResult {
        await Incapacitation.shared.stop()
        return .result()
    }
}

struct RedAlertIntent: AppIntent {
    static let title: LocalizedStringResource = "Make a red alert"
    
    func perform() async throws -> some IntentResult & ProvidesDialog {
        await Incapacitation.shared.redAlert(number: "113456")
        
        return .result(dialog: "make a red alert call")
    }
}

