//
//  IncapacitationLiveActivity.swift
//  Incapacitation
//
//  Created by thinh.vo on 26/8/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct IncapacitationAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        
        var duration: TimeInterval?
        
        func getTimeStringSinceNow() -> String {
            guard let duration = self.duration else {
                return "00:00:00"
            }
            return duration.format(using: [.hour, .minute, .second])
        }
        
        func getTimeIntervalSinceNow() -> Double {
            guard let duration = self.duration else {
                return 0
            }
            return duration
        }
    }
}

@available(iOS 17.0, *)
struct IncapacitationLiveActivity: Widget {
    
    @StateObject private var viewModel = Incapacitation()
    
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: IncapacitationAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack (alignment: .leading){
                HStack(alignment: .center) {
                    Image("app_logo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                    
                    VStack(alignment: .leading) {
                        Text("myprotect Mobile - now")
                            .font(.body)
                            .fontWeight(.bold)
                        HStack(spacing: 5){
                            Text("Incapacitation timer:")
                                .font(.body)
                                .fontWeight(.medium)
                                
//                            Text(context.state.getTimeStringSinceNow())
                            Text(
                                Date(timeInterval: context.state.getTimeIntervalSinceNow(), since: .now),
                                style: .timer)
                            .font(.body)
                            .fontWeight(.medium)
                            .contentTransition(.numericText())
                            .frame(width: 80.0)
                        }
                        .transition(.identity)
                    }
                    .activityBackgroundTint(Color.white)
                    .activitySystemActionForegroundColor(Color.black)
                }.frame(alignment: .leading)
                
                HStack {
                    IncapacitationActionView(context: context)
                }.padding()
            }
            
        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    
                }
                DynamicIslandExpandedRegion(.trailing) {
                    
                }
                DynamicIslandExpandedRegion(.center) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 24).strokeBorder(Color(red: 148/255.0, green: 163/255.0, blue: 184/255.0), lineWidth: 2)
                        HStack {
                            IncapacitationActionView(context: context)
                            
                            Text(
                                Date(timeInterval: context.state.getTimeIntervalSinceNow(), since: .now),
                                style: .timer)
                            .font(.title)
                            .foregroundColor(.cyan)
                            .fontWeight(.medium)
                            .monospacedDigit()
                            .contentTransition(.numericText())
                            .frame(width: 60)
                            
                        }
                        .padding()
                        .transition(.identity)
                        
                    }.padding()
                }
            } compactLeading: {
                Image("app_logo")
                    .imageScale(.medium)
                    .foregroundColor(.cyan)
            } compactTrailing: {
                Text(
                    Date(timeInterval: context.state.getTimeIntervalSinceNow(), since: .now),
                    style: .timer)
                .foregroundColor(.cyan)
                .monospacedDigit()
                .contentTransition(.numericText())
                .frame(maxWidth: 32)
                
            } minimal: {
                Image(systemName: "timer")
                    .imageScale(.medium)
                    .foregroundColor(.cyan)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

@available(iOS 17.0, *)
struct IncapacitationActionView : View {
    var context: ActivityViewContext<IncapacitationAttributes>
    var body: some View {
        
        HStack(spacing: 8.0, content: {
            
            Button(intent: ExtendTimerIntent()) {
                //                            ZStack {
                //                                Circle().fill(Color.cyan.opacity(0.5))
                //                                Image(systemName: "plus")
                //                                    .imageScale(.large)
                //                                    .foregroundColor(.cyan)
                //                            }
                Text("Extend")
                    .font(.body)
                    .fontWeight(.bold)
                    .foregroundColor(.black)
            }
            .buttonStyle(PlainButtonStyle())
            .contentShape(Rectangle())
            
            
            Button(intent: CancelTimerIntent()) {
                //                        ZStack {
                //                            Circle().fill(.gray.opacity(0.5))
                //                            Image(systemName: "xmark")
                //                                .imageScale(.medium)
                //                                .foregroundColor(.white)
                //                        }
                Text("Cancel")
                    .font(.body)
                    .fontWeight(.bold)
                    .foregroundColor(.black)
            }
            .buttonStyle(PlainButtonStyle())
            .contentShape(Rectangle())
            Spacer()
        })
    }
    
}

extension IncapacitationAttributes {
    fileprivate static var preview: IncapacitationAttributes {
        IncapacitationAttributes()
    }
}

extension IncapacitationAttributes.ContentState {
    fileprivate static var initial: IncapacitationAttributes.ContentState {
        IncapacitationAttributes.ContentState(duration: Date().timeIntervalSince1970)
    }
}

extension TimeInterval {
    func format(using units: NSCalendar.Unit) -> String {
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = units
        formatter.unitsStyle = .positional
        formatter.zeroFormattingBehavior = .pad
        return formatter.string(from: self) ?? ""
    }
}

@available(iOS 17.0, *)
#Preview("Notification", as: .content, using: IncapacitationAttributes.preview) {
    IncapacitationLiveActivity()
} contentStates: {
    IncapacitationAttributes.ContentState.initial
}
