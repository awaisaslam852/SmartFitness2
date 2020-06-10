package com.axles.smartfitness.engine.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class SplitDay(val index: Int): Parcelable {
	A(0),
	B(1),
	C(2),
	D(3),
	E(4),
	F(5);

	companion object {
		fun create(index: Int): SplitDay {
			val result = values().find { it.index == index }
			return result ?: A
		}
		fun create(title: String): SplitDay {
			val result = values().find { it.title() == title }
			return result ?: A
		}
	}

	fun title(): String {
		return when (index) {
			0 -> { "A" }
			1 -> { "B" }
			2 -> { "C" }
			3 -> { "D" }
			4 -> { "E" }
			5 -> { "F" }
			else -> { "A" }
		}
	}
}