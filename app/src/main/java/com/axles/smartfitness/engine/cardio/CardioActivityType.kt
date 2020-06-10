package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class CardioActivityType(val index: Int): Parcelable {
	RUNNING(0),
	CYCLING(1),
	ELLIPTICAL(2),
	ROWING_MACHINE(3),
	STAIR_CLIMBING(4),
	SWIMMING(5),
	JUMPING_ROPE(6);

	companion object {
		fun fromApiKey(key: String) = values().firstOrNull { it.apiKey() == key } ?: RUNNING
	}

	fun title(): String {
		return when (this) {
			RUNNING -> Helper.string(R.string.running)
			CYCLING -> Helper.string(R.string.cycling)
			ELLIPTICAL -> Helper.string(R.string.elliptical)
			ROWING_MACHINE -> Helper.string(R.string.rowing_machine)
			STAIR_CLIMBING -> Helper.string(R.string.stair_climbing)
			SWIMMING -> Helper.string(R.string.swimming)
			JUMPING_ROPE -> Helper.string(R.string.jumping_rope)
			else -> Helper.string(R.string.running)
		}
	}

	fun shortTitle(): String {
		return when (this) {
			RUNNING -> Helper.string(R.string.running)
			CYCLING -> Helper.string(R.string.cycling)
			ELLIPTICAL -> Helper.string(R.string.elliptical)
			ROWING_MACHINE -> Helper.string(R.string.rowing)
			STAIR_CLIMBING -> Helper.string(R.string.stair)
			SWIMMING -> Helper.string(R.string.swimming)
			JUMPING_ROPE -> Helper.string(R.string.jumping)
			else -> Helper.string(R.string.running)
		}
	}

	@DrawableRes fun iconResId(): Int {
		return when (this) {
			RUNNING -> R.drawable.cardio_running
			CYCLING -> R.drawable.cardio_cycling
			ELLIPTICAL -> R.drawable.cardio_elliptical
			ROWING_MACHINE -> R.drawable.cardio_rowing_machine
			STAIR_CLIMBING -> R.drawable.cardio_staircase_run
			SWIMMING -> R.drawable.cardio_swimming
			JUMPING_ROPE -> R.drawable.cardio_jumping_rope
			else -> R.drawable.cardio_running
		}
	}

	fun apiKey(): String {
		return when (this) {
			RUNNING -> "running"
			CYCLING -> "cycling"
			ELLIPTICAL -> "elliptical"
			ROWING_MACHINE -> "rowingMachine"
			STAIR_CLIMBING -> "stairClimbing"
			SWIMMING -> "swimming"
			JUMPING_ROPE -> "jumpingRope"
		}
	}
}