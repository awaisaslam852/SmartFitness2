package com.axles.smartfitness.extensions

fun Float.toRadian(): Float {
	return this * (Math.PI.toFloat() / 180.0).toFloat()
}