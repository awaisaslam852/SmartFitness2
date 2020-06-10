package com.axles.smartfitness.extensions

import com.axles.smartfitness.app.SmartFitnessApplication
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun Int.dpToPx(): Int{
    val density = SmartFitnessApplication.instance.applicationContext.resources.displayMetrics.density
    return (this.toDouble() * density).roundToInt()
}

fun Int.pxToDp(): Int{
    val density = SmartFitnessApplication.instance.applicationContext.resources.displayMetrics.density
    return (this.toDouble() / density).roundToInt()
}

fun Int.toTimerString(): String{
    val min = this / 60
    val sec = this % 60
    return String.format("%02d:%02d", min, sec)
}