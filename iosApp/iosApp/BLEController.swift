//
//  BLEManager.swift
//  iosApp
//
//  Created by thinh.vo on 6/9/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import MTBeaconPlus
import shared

class BLEController {
    
    var manager = MTCentralManager.sharedInstance()!
    var scannerDevices : Array<MTPeripheral> = []
    var currentPeripheral:MTPeripheral?
    var macId : String?
    
    let callCenter = CallCenterManager.shared.getCallCenter()
    let notifier = NotifierManager.shared.getLocalNotifier()
    
    static let shared = BLEController()
    
    func startScan(macId: String?) -> Void {
        self.macId = macId
        
        if (manager.scanning){
            manager.stopScan()
        }
        
        Logger.shared.d(message: "startScan \(macId ?? "")", tag: "")
        
        manager.stateBlock = { (state) in
            
            switch state {
                
            case .unknown:
                Logger.shared.d(message: "the iphone bluetooth state unknown", tag: "")
            case .resetting:
                Logger.shared.d(message: "the iphone bluetooth state resetting", tag: "")
            case .unsupported:
                Logger.shared.d(message: "the iphone bluetooth state unsupported", tag: "")
            case .unauthorized:
                Logger.shared.d(message: "the iphone bluetooth state unauthorized", tag: "")
            case .poweredOff:
                Logger.shared.d(message: "the iphone bluetooth state off", tag: "")
                
                if let p = self.currentPeripheral {
                    IOSBLEManager.Companion.shared.setMTPeripherals(
                        peripherals: [BLEPeripheral(
                            mac: p.framer.mac ?? "",
                            name: p.framer.name ?? "" ,
                            battery: Int32(p.framer.battery),
                            active: false
                        )]
                    )
                }
            case .poweredOn:
                Logger.shared.d(message: "the iphone bluetooth state on", tag: "")
                //
                break
            @unknown default:
                Logger.shared.d(message: "the iphone bluetooth state default", tag: "")
            }
            
        }
        
        manager.startScan { (devices) in
            
            Logger.shared.d(message: "scanning", tag: "BLEController")
            
            self.scannerDevices = self.manager.scannedPeris
            
            self.currentPeripheral = self.scannerDevices.first(where: {$0.framer.mac == macId})
            
            let deviceConnectedNotAvailable = (!(macId ?? "").isEmpty) && self.currentPeripheral == nil
            
            if deviceConnectedNotAvailable {
                
                IOSBLEManager.Companion.shared.setMTPeripherals(
                    peripherals: [BLEPeripheral(
                        mac: macId ?? "",
                        name: "" ,
                        battery: 0,
                        active: false
                    )]
                )
                return
            }
            
            let peripherals = self.scannerDevices.filter({$0.framer?.mac != nil}).map {peripheral in
                
                if(peripheral.framer.mac == macId){
                    for advFrame in peripheral.framer.advFrames {
                        
                        switch(advFrame.frameType){
                        case .FrameiBeacon :
                            break
                        case .FrameUID :
                            break
                        case .FrameURL :
                            self.onTriggered(lastUpdate: advFrame.lastUpdate)
                            break
                        default:
                            break
                        }
                    }
                }
                
                return BLEPeripheral(
                    mac: peripheral.framer.mac ?? "",
                    name: peripheral.framer.name ?? "Unnamed" ,
                    battery: Int32(
                        peripheral.framer.battery
                    ),
                    active: true
                )
            }
            IOSBLEManager.Companion.shared.setMTPeripherals(
                peripherals: peripherals
            )
        }
        
    }
    
    func onTriggered(lastUpdate: Date) -> Void {
       
        let currentTimeMillis = Date().currentTimeMillis()
        let lastTimeMillis = lastUpdate.currentTimeMillis()
        
        if(currentTimeMillis - lastTimeMillis <= 5){
            
            Logger.shared.d(message: "Trippe Tap Trigger", tag: "BLEController")
            
            callCenter.makeRedAlertCall()
            
            notifier.notify(id: 13, title: "Bluetooth button", body: "Triple Tap Trigger", payloadData: [:])
        }
    }
    
    func stopScan() -> Void {
        macId = nil
        manager.stopScan()
    }
    
    func connectDevice(macId : String) {
        self.macId = macId
        if let peripheral = self.scannerDevices.first(where: {$0.framer.mac == macId}){
            self.currentPeripheral = peripheral
            self.connectDevice(peripheral: peripheral)
        }
    }
    
    func connectDevice(peripheral : MTPeripheral) -> Void {
        peripheral.connector.statusChangedHandler = { (status, error) in
            
            if error != nil {
                Logger.shared.e(message: error as Any as! String, tag: "")
            }
            
            switch status {
            case .StatusCompleted:
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1.0, execute: {
                    
                    // After the connection
                    IOSBLEManager.Companion.shared.setDeviceEvent(event: DeviceEventOnDeviceConnected(macId: self.macId ?? ""))
                    
                })
                
                self.writeFrame(peripheral: peripheral)
                
                break
                
            case .StatusDisconnected:
                
                Logger.shared.d(message: "disconnected", tag: "")
                
                IOSBLEManager.Companion.shared.setDeviceEvent(event: DeviceEventOnDeviceDisconnected(macId: self.macId ?? ""))
                
                break
                
            case .StatusConnectFailed:
                
                Logger.shared.d(message: "connect failed", tag: "")
                
                IOSBLEManager.Companion.shared.setDeviceEvent(event: DeviceEventOnDeviceConnectFailed())
                
                break
                
            case .StatusUndifined:
                break
                
            default:
                break
            }
        }
        
        manager.connect(toPeriperal:peripheral, passwordRequire: { (pass) in
            
            pass!("minew123")
        })
    }
    // MARK: ---------------------------disconnectDevice
    
    func disconnect(macId : String) {
        if let peripheral = self.scannerDevices.first(where: {$0.framer.mac == macId}){
            self.disconnect(peripheral: peripheral)
        }
    }
    
    func disconnect(peripheral : MTPeripheral) -> Void {
        manager.disconnect(fromPeriperal: peripheral)
    }
    
    func getAllBroadcast() -> Void {
        for frame in (currentPeripheral?.framer.advFrames)! {
            self.getBroadcastFrame(frame: frame)
        }
    }
    
    func getAllSlot() -> Void {
        for frame in (currentPeripheral?.connector.allFrames)! {
            self.getSlotFrame(frame: frame)
        }
    }
    
    func getBroadcastFrame(frame:MinewFrame) -> Void {
        switch frame.frameType {
        case .FrameiBeacon:
            let iBeacon = frame as! MinewiBeacon
            
            let timeFormatter = DateFormatter()
            timeFormatter.dateFormat = "yyy-MM-dd' at 'HH:mm:ss"
            let strNowTime = timeFormatter.string(from: iBeacon.lastUpdate) as String
            
            Logger.shared.d(message: "iBeacon:\(iBeacon.major)--\(String(describing: iBeacon.uuid))--\( iBeacon.minor)--\(strNowTime)", tag: "")
            break
        case .FrameURL:
            let url = frame as! MinewURL
            Logger.shared.d(message: "URL:\(url.urlString ?? "nil")--\(233)", tag: "")
            break
        case .FrameUID:
            let uid = frame as! MinewUID
            Logger.shared.d(message: "UID:\(uid.namespaceId ?? "nil")--\(uid.instanceId ?? "nil")", tag: "")
            break
        case .FrameTLM:
            let tlm = frame as! MinewTLM
            
            let timeFormatter = DateFormatter()
            timeFormatter.dateFormat = "yyy-MM-dd' at 'HH:mm:ss"
            let strNowTime = timeFormatter.string(from: tlm.lastUpdate) as String
            
            Logger.shared.d(message: "TLM--\(strNowTime)", tag: "")
            break
        case .FrameDeviceInfo:
            Logger.shared.d(message: "DeviceInfo", tag: "")
            break
        case .FrameLineBeacon:
            let lineBeaconData = frame as! MinewLineBeacon
            Logger.shared.d(message: "LineBeacon:\(lineBeaconData.hwId)", tag: "")
            break
            
        case .FrameMBeaconInfo:
            let mBeaconInfo = frame as! MinewMBeaconInfo
            Logger.shared.d(message: "miniBeaconInfo:\(mBeaconInfo.battery)", tag: "")
            break
            
        default:
            Logger.shared.d(message: "Unauthenticated Frame", tag: "")
            break
        }
    }
    
    func getSlotFrame(frame:MinewFrame) -> Void {
        
        switch frame.frameType {
        case .FrameiBeacon:
            let iBeacon = frame as! MinewiBeacon
            Logger.shared.d(message: "iBeacon:\(iBeacon.major)--\(String(describing: iBeacon.uuid))--\( iBeacon.minor)", tag: "")
            break
        case .FrameURL:
            let url = frame as! MinewURL
            Logger.shared.d(message: "SlotFrame---URL:\(url.urlString ?? "nil")", tag: "")
            break
        case .FrameUID:
            let uid = frame as! MinewUID
            Logger.shared.d(message: "SlotFrame---UID:\(uid.namespaceId ?? "nil")--\(uid.instanceId ?? "nil")", tag: "")
            break
        case .FrameTLM:
            Logger.shared.d(message: "SlotFrame---TLM", tag: "")
            break
        case .FrameDeviceInfo:
            Logger.shared.d(message: "SlotFrame---DeviceInfo", tag: "")
            break
            // LineBeacon
        case .FrameLineBeacon:
            let lineBeaconData = currentPeripheral?.connector.slotHandler.slotFrameDataDic[FrameTypeString(FrameType.FrameLineBeacon)!] as? MTLineBeaconData
            Logger.shared.d(message: "SlotFrame---LineBeacon:\(String(describing: lineBeaconData?.lotKey))--\(lineBeaconData?.hwId ?? "")--\(String(describing: lineBeaconData?.vendorKey))", tag: "")
            break
            
        case .FrameSixAxisSensor:
            Logger.shared.d(message: "SlotFrame---SixAxisSensor", tag: "")
            //            self.getSensorHistory()
            break
            
        default:
            Logger.shared.d(message: "SlotFrame---Unauthenticated Frame", tag: "")
            break
        }
    }
    
    func writeFrame(peripheral : MTPeripheral) -> Void {
        
        let allFrames = peripheral.connector.allFrames
        let slotAtitude = peripheral.connector.feature.slotAtitude
        
        Task {
            
            for i in 0...slotAtitude - 1 {
                
                if let minewFrame = allFrames?[i] as? MinewURL {
                    minewFrame.urlString = "http://www.sos/"
                    
                    let success = try await peripheral.connector.write(minewFrame)
                    
                    if success {
                        Logger.shared.d(message: "write success \(String(describing: minewFrame.urlString)) \(minewFrame.slotRadioTxpower)", tag: "")
                    }
                    
                    await self.writeTrigger(peripheral: peripheral, slot: i, isOpen: true)
                    
                } else {
                    
                    await self.writeTrigger(peripheral: peripheral, slot: i)
                    
                    
//                    if let minewFrame = allFrames?[i] {
//                        let ib = MinewFrame.init()
//                        ib.frameType = FrameType.FrameNone
//                        ib.slotNumber = i
//                        
//                        let success = try await peripheral.connector.write(ib)
//                        
//                        if success {
//                            Logger.shared.d(message: "write success \(minewFrame.slotRadioTxpower)",  tag: "")
//                        }
//                    }
                }
                
            }
            
            DispatchQueue.main.async {
                self.disconnect(peripheral: peripheral)
                self.startScan(macId: peripheral.framer.mac)
            }
        }
    }
    
    func setLineBeacon() -> Void {
        
        currentPeripheral?.connector.slotHandler.lineBeaconSetLotkey("0011223344556677", completion: { (success) in
            if success == true {
                Logger.shared.d(message: "Set LineBeacon's lotKey success", tag: "")
            } else {
                Logger.shared.e(message: "Set LineBeacon's lotKey fail", tag: "")
            }
        })
        
        currentPeripheral?.connector.slotHandler.lineBeaconSetHWID("0011223344", vendorKey: "00112233", completion: { (success) in
            if success == true {
                Logger.shared.d(message: "Set LineBeacon's hwid and vendorKey success", tag: "")
            } else {
                Logger.shared.d(message: "Set LineBeacon's hwid and vendorKey fail", tag: "")
            }
        })
        
    }
    
    func getTrigger() -> Void {
        let triggerData:MTTriggerData =  currentPeripheral?.connector.triggers[1] ?? MTTriggerData()
        
        Logger.shared.d(message: "TriggerData \n type:\(triggerData.type.rawValue)--advertisingSecond:\(String(describing: triggerData.value))--alwaysAdvertise:\( triggerData.always)--advInterval:\(triggerData.advInterval)--radioTxpower:\(triggerData.radioTxpower)", tag: "")
        
    }
    
    func writeTrigger(peripheral : MTPeripheral, slot: Int, isOpen: Bool = false) async -> Void {
        let triggerType = isOpen ? TriggerType.btnTtapLater : TriggerType.none
        
        // Tips:Use the correct initialization method for MTTriggerData
        let triggerData = MTTriggerData.init(
            slot: slot,
            paramSupport: isOpen,
            triggerType: triggerType,
            value: isOpen ? 10 : 0
        )
        
        if (isOpen) {
            triggerData?.always = false;
            triggerData?.advInterval = 2000;
            triggerData?.radioTxpower = 0;
        }
        
        let success = await peripheral.connector.writeTrigger(triggerData)
        if success {
            Logger.shared.d(message: "write triggerData \(slot) success", tag: "")
        } else {
            Logger.shared.e(message: "write triggerData \(slot) failed", tag: "")
        }
        
    }
    
    func getSensorHistory() {
        
        self.currentPeripheral?.connector.sensorHandler.readSixAxisSensorHistory({ (data) in
            let sensorData = data as! MTSensorSixAxisData
            
            if sensorData.endStatus == .none {
                
                if sensorData.sixAxisType == .acc {
                    Logger.shared.d(message: "Acc:\(sensorData.accXAxis)-\(sensorData.accYAxis)-\(sensorData.accZAxis)", tag: "")
                } else if sensorData.sixAxisType == .deg {
                    Logger.shared.d(message: "Deg:\(sensorData.degXAxis)-\(sensorData.degYAxis)-\(sensorData.degZAxis)", tag: "")
                }
            }
            else if sensorData.endStatus == .success {
                // Success
                
            }
            else if sensorData.endStatus == .error {
                // Failed
                
            }
        })
    }
    
    func vibrationSensitivity() {
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.1) {
            
            //            MTSensitivityTypeSuperHigh,
            //            MTSensitivityTypeHigh,
            //            MTSensitivityTypeMiddle,
            //            MTSensitivityTypeLow,
            //            MTSensitivityTypeSuperLow,
            //            MTSensitivityTypeError,
            self.currentPeripheral?.connector.sensorHandler.setSensitivity(.superHigh, completionHandler: { (isSuccess) in
                
            })
        }
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            
            self.currentPeripheral?.connector.sensorHandler.querySensitivity({ (isSuccess, type) in
                
            })
        }
        
    }
    
    func vibrationType() {
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.3) {
            
            self.currentPeripheral?.connector.sensorHandler.setVibrationStatus(.open, completionHandler: { (isSuccess) in
                
            })
        }
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.4) {
            
            //            MTVibrationTypeOpen,
            //            MTVibrationTypeClose,
            //            MTVibrationTypeError,
            self.currentPeripheral?.connector.sensorHandler.queryVibrationStatu({ (isSuccess, type) in
                
                
            })
        }
    }
    
    func vibrationTimeZone() {
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.5) {
            
            self.currentPeripheral?.connector.sensorHandler.setFirstAlarmTimeIntervalWithAlarmStatus(.open, startTime: 21060, endTime: 79260, completionHandler: { isSuccess in
                
            })
        }
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.6) {
            
            self.currentPeripheral?.connector.sensorHandler.queryFirstAlarmTimeInterval({ isSuccess, type, startTime, endTime in
                // timeZone = hours*3600 + minutes*60
                Logger.shared.d(message: "queryAlarmTimeZone:\(isSuccess)--\(startTime):\(startTime/3600)hour \(startTime%3600/60)minute--\n--\(endTime):\(endTime/3600)hour \(endTime%3600/60)minute", tag: "")
                
            })
            
        }
    }
    
    func setBroadcastRate() {
        // When setting the broadcast rate, the incoming array has six values, and each value corresponds to six channels. 0 means that the broadcast rate of Bluetooth 4.0 is 1M hz, and 1 means that the broadcast rate of Bluetooth 5.0 is 125k hz.
        // It should be noted that iOS currently cannot obtain broadcast packets with a broadcast frequency of 125 kHz, and Android does not have this problem. That is, the frame channel of the Bluetooth 5.0 broadcast rate is set to 125kHz on iOS. After the setting is completed, the frame cannot be scanned on the iPhone, but the frame can be scanned on the Android.
        self.currentPeripheral?.connector.sensorHandler.queryBroadcastSpeed({ isSuccess, broadcastSpeedOfSlotArray in
            
            Logger.shared.d(message: "The broadcast rate of the first channel is" +  (broadcastSpeedOfSlotArray[0] as! Int == 1 ? "Bluetooth5.0 125kHz" : "Bluetooth4.0 1M"), tag: "")
        })
        
        // When setting the broadcast rate, the incoming array has six values, and each value corresponds to six channels. 0 means that the broadcast rate of Bluetooth 4.0 is 1M, and 1 means that the broadcast rate of Bluetooth 5.0 is 125KHz. For example, if the first and second channels are set to 125KHz broadcast, and the other channels are set to 1M broadcast, then the settings are as follows:
        self.currentPeripheral?.connector.sensorHandler.setBroadcastSpeed([1, 1, 0, 0, 0, 0], completionHandler: { isSuccess in
            
        })
        
    }
    
    func setParmForSpecialAccSensor() {
        
        self.currentPeripheral?.connector.sensorHandler.queryAccSensorParameter(completionHandler: { isSuccess, odr, wakeupThreshold, wakeupDuration in
            
        })
        
        /**
         ODR ( Output rate ) : 0,1,2,3,4,5,6,7,8 correspond to 1Hz, 10Hz, 25Hz, 50Hz, 100Hz, 200Hz, 400Hz, 1600Hz ( low power ) , 1344HZ ( HR/normal ) / 5000HZ ( low power ) respectively
         wakeup_threshold ( mg ) : 0 ~ 2000
         wakeup_duration  ( ms ) : 0 ~ 127000
         */
        self.currentPeripheral?.connector.sensorHandler.setAccSensorParameterWithOdr(0, wakeupThreshold: 200, wakeupDuration: 1000, completionHandler: { isSuccess in
            
        })
        
    }
}
