package com.axles.smartfitness.engine.program

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ProgramType(val index: Int): Parcelable {
	RESISTANCE(0),
	CIRCUIT(1),
	CARDIO(2);

	companion object {
		fun fromIndex(index: Int): ProgramType {
			return values().firstOrNull { it.index == index } ?: RESISTANCE
		}

		fun fromTitle(title: String): ProgramType {
			return values().firstOrNull { it.title() == title } ?: RESISTANCE
		}
	}

	fun title(): String {
		return when(index) {
			0 -> Helper.string(R.string.resistance_training)
			1 -> Helper.string(R.string.circuit_training)
			2 -> Helper.string(R.string.cardio_training)
			else -> Helper.string(R.string.resistance_training)
		}
	}

	fun apiKey(): String {
		return when(index) {
			0 -> "resistance"
			1 -> "circuit"
			else -> "cardio"
		}
	}

	@DrawableRes
	fun iconResource(): Int {
		return when(index) {
			0 -> R.drawable.icon_biceps
			1 -> R.drawable.icon_circuit
			2 -> R.drawable.icon_man
			else -> R.drawable.icon_biceps
		}
	}
}