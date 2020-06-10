package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import com.axles.smartfitness.engine.cardio.CardioActivity
import kotlinx.android.parcel.Parcelize

@Parcelize class RunningActivity: CardioActivity(CardioActivityType.RUNNING), Parcelable {
	var speed : Double = 0.0
	var distance : Double = 0.0
	var incline : Double = 0.0
	var paceSeconds: Int = 0

	override fun isEdited(): Boolean {
		if (timeSeconds != 0) {
			return true
		}

		if (speed != 0.0){
			return true
		}

		if (distance != 0.0){
			return true
		}

		if (incline != 0.0){
			return true
		}

		if (paceSeconds != 0){
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return RunningActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.speed = this.speed
			it.incline = this.incline
			it.paceSeconds = this.paceSeconds
		}
	}
}