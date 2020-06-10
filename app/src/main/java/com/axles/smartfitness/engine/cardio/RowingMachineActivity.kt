package com.axles.smartfitness.engine.cardio

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioActivity.*
import kotlinx.android.parcel.Parcelize

@Parcelize class RowingMachineActivity: CardioActivity(CardioActivityType.ROWING_MACHINE), Parcelable {
	enum class Intensity(@StringRes val intensityRes: Int) {
		MEDIUM(R.string.medium),
		EASY(R.string.easy),
		HARD(R.string.hard),
		RACE_PACE(R.string.race_pace),
		WARM_UP(R.string.warm_up),
		COOL_DOWN(R.string.cool_down),
		REST(R.string.rest);

		companion object {
			fun fromString(context: Context, intensity: String): Intensity {
				return when (intensity) {
					context.getString(MEDIUM.intensityRes) -> MEDIUM
					context.getString(EASY.intensityRes) -> EASY
					context.getString(HARD.intensityRes) -> HARD
					context.getString(RACE_PACE.intensityRes) -> RACE_PACE
					context.getString(WARM_UP.intensityRes) -> WARM_UP
					context.getString(COOL_DOWN.intensityRes) -> COOL_DOWN
					context.getString(REST.intensityRes) -> REST
					else -> MEDIUM
				}
			}
		}

		fun apiKey(): String {
			return when (this) {
				MEDIUM -> "Medium"
				EASY -> "Easy"
				HARD -> "Hard"
				RACE_PACE -> "Race Pace"
				WARM_UP -> "Warm Up"
				COOL_DOWN -> "Cool Down"
				REST -> "Rest"
				else -> ""
			}
		}
	}

	var intensity : Intensity = Intensity.MEDIUM
	var distance : Double = 0.0
	var paceSeconds : Int = 0
	var spmFirst: Int = 0
	var spmSecond: Int = 0

	override fun isEdited(): Boolean {

		if (timeSeconds != 0) {
			return true
		}

		if (intensity != Intensity.MEDIUM){
			return true
		}

		if (distance != 0.0){
			return true
		}

		if (paceSeconds != 0){
			return true
		}

		if (spmFirst != 0 || spmSecond != 0){
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return RowingMachineActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.intensity = this.intensity
			it.paceSeconds = this.paceSeconds
			it.spmFirst = this.spmFirst
			it.spmSecond = this.spmSecond
		}
	}
}