package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import com.axles.smartfitness.engine.cardio.CardioActivity
import kotlinx.android.parcel.Parcelize

@Parcelize class StairClimbingActivity: CardioActivity(CardioActivityType.STAIR_CLIMBING), Parcelable {

	var steps : Int = 0
	var resistance : Int = 1
	var spmFirst: Int = 0
	var spmSecond: Int = 0

	override fun isEdited(): Boolean {

		if (timeSeconds != 0) {
			return true
		}

		if (resistance != 1){
			return true
		}

		if (steps != 0){
			return true
		}

		if (spmFirst != 0 || spmSecond != 0){
			return true
		}

		return false

	}

	override fun copy(): CardioActivity {
		return StairClimbingActivity().also {
			it.timeSeconds = this.timeSeconds
			it.valueType = this.valueType

			it.steps = this.steps
			it.resistance = this.resistance
			it.spmFirst = this.spmFirst
			it.spmSecond = this.spmSecond
		}
	}

}