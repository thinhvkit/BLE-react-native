//
//  ActionShortcut.swift
//  iosApp
//
//  Created by thinh.vo on 21/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import AppIntents

struct ActionShortcuts: AppShortcutsProvider {
    
    static var appShortcuts: [AppShortcut] {
        
        AppShortcut(
            intent: ActionIntent(),
            phrases: [
                "\(.applicationName) Red Alert"
            ],
            shortTitle: LocalizedStringResource("Red Alert"),
            systemImageName: "phone"
        )
    }
}
