package com.axles.smartfitness.extensions

import java.lang.Double.parseDouble
import java.lang.Exception

fun Any.isNumber(): Boolean {
	if (this is Number) { return true }
	if (this is String) {
		try {
			val number = parseDouble(this)
		} catch (e: Exception) {
			return false
		}
	}
	return true
}

fun Any.isTimeText(): Boolean {
	return (this is String) && this.contains(":")
}