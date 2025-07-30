package com.myprotect.projectx.common

import androidx.compose.runtime.Composable
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

interface AccelerometerInterface {
    val x: Double
    val y: Double
    val z: Double
}

data class AccelerometerData(
    override val x: Double,
    override val y: Double,
    override val z: Double
) : AccelerometerInterface


interface SensorDataInterface {
    val heading: Double
    val sensor: AccelerometerInterface
    val gravity: AccelerometerInterface?
}

data class SensorData(
    override val heading: Double,
    override val sensor: AccelerometerInterface,
    override val gravity: AccelerometerInterface? = null
) : SensorDataInterface

interface SensorInterface {
    val data: CommonFlow<SensorDataInterface?>
    val isEnabled: Boolean
    fun start()
    fun stop()
}

expect class Sensors : SensorInterface {
    override val data: CommonFlow<SensorDataInterface?>
    override val isEnabled: Boolean
    override fun start()
    override fun stop()
}

@Composable
expect fun createSensors(): Sensors


fun fallDetection(
    accelerationReaders: ArrayList<Double>,
    acc: Triple<Double, Double, Double>
): Boolean {
    val loX = acc.first
    val loY = acc.second
    val loZ = acc.third

    val loAccelerationReader = sqrt(
        loX.pow(2.0) + loY.pow(2.0) + loZ.pow(2.0)
    )

    val ldAccRound = loAccelerationReader.roundTo(2)

    // precision/fall detection and more than 1000ms after last fall
    // for easy to testing
    return ldAccRound > 0.4 && ldAccRound < 4.0

    // uncomment when running production
    accelerationReaders.add(ldAccRound)

    Logger.d(accelerationReaders.toString())

    val n: Int = accelerationReaders.size
    for (i in 0 until n) {
        if (accelerationReaders[i] < SensorParameter.lowThreshold) {
            for (j in i + 1 until n) {
                if (accelerationReaders[j] > SensorParameter.highThreshold) {
                    accelerationReaders.clear()
                    return true
                }
            }
        }
    }
    return false
}

fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

class SensorParameter {
    companion object {
        const val highThreshold = 0.7F
        const val lowThreshold = 0.3F
    }
}
