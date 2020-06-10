package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize class EllipticalActivity: CardioActivity(CardioActivityType.ELLIPTICAL), Parcelable {

	var resistance : Int = 1
	var distance : Double = 0.0
	var incline : Double = 0.0
	var spmFirst: Int = 0
	var spmSecond: Int = 0

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

		if (incline != 0.0){
			return true
		}

		if (spmFirst != 0 || spmSecond != 0){
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return EllipticalActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.resistance = this.resistance
			it.incline = this.incline
			it.spmFirst = this.spmFirst
			it.spmSecond = this.spmSecond
		}
	}
}