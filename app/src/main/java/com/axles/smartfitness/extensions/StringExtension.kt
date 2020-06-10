package com.axles.smartfitness.extensions

import java.lang.Exception
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.timerStringToLong(): Long {
	return if (this.contains(":")) {
		val minutes = this.substringBefore(":")
		val seconds = this.substringAfter(":")
		minutes.toLong().times(60).plus(seconds.toLong())
	} else 0L
}

fun String.toSecond(): Int {
	return if (this.contains(":")) {
		val minutes = firstInt()
		val seconds = secondInt()
		minutes.times(60).plus(seconds)
	} else 0
}

fun String.firstInt(): Int {
	try {
		if (contains(":")) { return substringBefore(":").trim().toInt() }
		if (contains("-")) { return substringBefore("-").trim().toInt() }
		return toInt()
	} catch (e: Exception) {
		e.printStackTrace()
	}
	return 0
}

fun String.secondInt(): Int {
	try {
		if (contains(":")) { return substringAfter(":").trim().toInt() }
		if (contains("-")) { return substringAfter("-").trim().toInt() }
		return toInt()
	} catch (e: Exception) {
		e.printStackTrace()
	}
	return 0
}


fun String.toSafeDouble(): Double {
	try {
		return this.toDouble()
	} catch (e: Exception) {
		e.printStackTrace()
	}
	return 0.0
}

fun Double.format(decimalPlaces: Int): String =
	DecimalFormat(getDecimalPattern(decimalPlaces)).format(this)


private fun getDecimalPattern(decimalPlaces: Int): String {
	return buildString {
		append("0.")
		repeat(decimalPlaces) {
			append("0")
		}
	}
}

private const val DayWrittenMonthYear = "dd MMMM yyyy"

fun Date.formatTo(pattern: String = DayWrittenMonthYear): String {
	val locale = Locale.getDefault()
	val dateFormat: DateFormat = SimpleDateFormat(pattern, locale)
	return dateFormat.format(this)
}