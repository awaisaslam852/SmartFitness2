package com.axles.smartfitness.extensions

/**
 * converts @this long to timer string format : 00:00
 */
fun Long.toTimerString(): String{
    val min = this / 60L
    val sec = this - (min * 60L)
    return String.format("%02d:%02d", min, sec)
}