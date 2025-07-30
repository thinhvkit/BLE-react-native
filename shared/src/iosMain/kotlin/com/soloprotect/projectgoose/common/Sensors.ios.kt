package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import platform.CoreMotion.CMAcceleration
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

@Composable
actual fun createSensors(): Sensors {
    return Sensors()
}

actual class Sensors : SensorInterface {
    private var _data = Channel<SensorDataInterface?>(Channel.BUFFERED)

    actual override val data: CommonFlow<SensorDataInterface?>
        get() = _data.consumeAsFlow().asCommonFlow()
    private var _isEnabled = false
    actual override val isEnabled: Boolean
        get() = _isEnabled

    private val scope = CoroutineScope(UIDispatcher())
    private val queue = NSOperationQueue.mainQueue
    private val manager = CMMotionManager().apply {
        deviceMotionUpdateInterval = 20 / 1000.0
    }

    actual override fun start() {
        _isEnabled = true
        manager.startDeviceMotionUpdatesUsingReferenceFrame(
            CMAttitudeReferenceFrameXMagneticNorthZVertical, queue
        ) { motion, error ->
            if (error != null) {
                return@startDeviceMotionUpdatesUsingReferenceFrame
            }
            motion?.let {
                scope.launch {
                    val converted = convertIntoSensorData(it)
                    _data.send(converted)
                }
            }
        }
    }

    actual override fun stop() {
        _isEnabled = false
        manager.stopDeviceMotionUpdates()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun convertIntoSensorData(motion: CMDeviceMotion?): SensorDataInterface? {
        if (motion == null) return null
        val user = motion.userAcceleration
        val gravity = motion.gravity
        val heading = motion.heading
        return SensorData(heading, convertAcceleration(user), convertAcceleration(gravity))
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun convertAcceleration(acceleration: CValue<CMAcceleration>): AccelerometerInterface {
        acceleration.useContents {
            return AccelerometerData(this.x, this.y, this.z)
        }
    }
}
