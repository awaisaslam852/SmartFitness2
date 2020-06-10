package com.axles.smartfitness.cardio_training

import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.CardioActivityType

data class CardioActivityModel(val activityType: CardioActivityType,
							   var stateType: CardioActivity.StateType = CardioActivity.StateType.INACTIVE): Comparable<CardioActivityModel> {
	override fun compareTo(other: CardioActivityModel): Int {
		if (this.stateType == other.stateType) { return this.activityType.index - other.activityType.index }
		if (this.stateType == CardioActivity.StateType.ACTIVATED) { return -1 }
		if (other.stateType == CardioActivity.StateType.ACTIVATED) { return 1 }
		return this.activityType.index - other.activityType.index
	}
}