package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CardioActivity(
	val type: CardioActivityType,
	var timeSeconds: Int = 0,
	var valueType: ValueType = ValueType.TIME
): Parcelable {
	enum class StateType {
		INACTIVE,
		ACTIVATED,
		SELECTED
	}

	enum class ValueType {
		NONE,
		TIME,
		DISTANCE
	}

	companion object {
		fun create(activityType: CardioActivityType): CardioActivity {
			return when (activityType) {
				CardioActivityType.RUNNING -> RunningActivity()
				CardioActivityType.CYCLING -> CyclingActivity()
				CardioActivityType.ELLIPTICAL -> EllipticalActivity()
				CardioActivityType.ROWING_MACHINE -> RowingMachineActivity()
				CardioActivityType.STAIR_CLIMBING -> StairClimbingActivity()
				CardioActivityType.SWIMMING -> SwimmingActivity()
				CardioActivityType.JUMPING_ROPE -> JumpingRopeActivity()
				else -> RunningActivity()
			}
		}
	}

	open fun isEdited(): Boolean { return false }
	open fun copy(): CardioActivity { return this }
}