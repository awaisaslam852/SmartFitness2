package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize class CyclingActivity: CardioActivity(CardioActivityType.CYCLING), Parcelable {

	var resistance : Int = 1
	var rpmFirst = 0
	var rpmSecond = 0

	var distance : Double = 0.0

	override fun isEdited(): Boolean {

		if (timeSeconds != 0) {
			return true
		}

		if (resistance != 1){
			return true
		}

		if (distance != 0.0){
			return true
		}

		if (rpmFirst != 0 || rpmSecond != 0){
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return CyclingActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.resistance = this.resistance
			it.rpmFirst = this.rpmFirst
			it.rpmSecond = this.rpmSecond
		}
	}
}