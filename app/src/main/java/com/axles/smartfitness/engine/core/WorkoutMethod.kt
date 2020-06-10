package com.axles.smartfitness.engine.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize enum class WorkoutMethod(val index: Int): Parcelable {
	DEFAULT(0),
	DROP_SET(1),
	PYRAMID(2),
	PERCENT(3),
	SAME_MUSCLE_SUPERSET(4),
	DIFFERENT_MUSCLE_SUPERSET(5);

	companion object {
		fun fromIndex(index: Int): WorkoutMethod {
			return values().first { it.index == index } ?: DEFAULT
		}
	}

	fun title(): String {
		return when (index) {
			0 -> "DEFAULT"
			1 -> "Drop Set"
			2 -> "Pyramid"
			3 -> "Percentage"
			4 -> "Super Set"
			5 -> "Super Set"
			else -> "DEFAULT"
		}
	}

	fun apiKey(): String {
		return when (index) {
			0 -> "none"
			1 -> "dropSet"
			2 -> "pyramid"
			3 -> "percentOfRM"
			4 -> "superset"
			5 -> "supersetDifferent"
			else -> "none"
		}
	}

	fun isSuperSet(): Boolean {
		return (this == SAME_MUSCLE_SUPERSET || this == DIFFERENT_MUSCLE_SUPERSET)
	}
}