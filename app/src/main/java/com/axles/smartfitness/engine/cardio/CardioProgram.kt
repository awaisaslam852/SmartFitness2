package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import kotlinx.android.parcel.Parcelize

@Parcelize class CardioProgram: Program(ProgramType.CARDIO), Parcelable {

	val activities = mutableMapOf<CardioActivityType, MutableList<CardioActivity>>()

	override fun isNew(): Boolean {
		return activities.isEmpty()
	}

	fun clear() {
		activities.clear()
	}

	fun clear(activityType: CardioActivityType) {
		activities[activityType]?.clear()
		activities.remove(activityType)
	}

	fun hasType(type: CardioActivityType): Boolean {
		return activities[type] != null
	}

	fun activitiesByType(type: CardioActivityType): List<CardioActivity> {
		return activities[type] ?: listOf()
	}

	fun lastActivity(activityType: CardioActivityType): CardioActivity? {
		return activities[activityType]?.last()
	}

	fun valueTypeBy(activityType: CardioActivityType): CardioActivity.ValueType {
		val curActivities = activitiesByType(activityType).toList()
		if (curActivities.isEmpty()) { return CardioActivity.ValueType.NONE }
		val result = curActivities.first().valueType
		for (index in 1 until curActivities.size) {
			if (result != curActivities[index].valueType) { return CardioActivity.ValueType.NONE }
		}
		return result
	}

	fun totalTime(type: CardioActivityType): Int {
		if (activities.containsKey(type)) {
			val activities = this.activities[type]!!
			var result = 0
			activities.forEach {
				if (it.valueType == CardioActivity.ValueType.TIME) {
					result += it.timeSeconds
				}
			}
			return result
		}
		return 0
	}

	fun totalDistance(type: CardioActivityType): Double {
		if (activities.containsKey(type)) {
			val activities = this.activities[type]!!
			var result = 0.0
			activities.forEach {
				if (it.valueType == CardioActivity.ValueType.DISTANCE) {
					when (it) {
						is RunningActivity -> { result += it.distance }
						is CyclingActivity -> { result += it.distance }
						is EllipticalActivity -> { result += it.distance }
						is RowingMachineActivity -> { result += it.distance }
						is SwimmingActivity -> { result += it.distance }
					}
				}
			}
			return result
		}
		return 0.0
	}

	private fun addActivity(activity: CardioActivity) {
		val type = activity.type
		if (!activities.containsKey(type)) { activities[type] = mutableListOf() }
		activities[type]?.add(activity)
	}

	fun addEmptyActivity(activityType: CardioActivityType) {
		when (activityType) {
			CardioActivityType.RUNNING -> { addActivity(RunningActivity()) }
			CardioActivityType.CYCLING -> { addActivity(CyclingActivity()) }
			CardioActivityType.ELLIPTICAL -> { addActivity(EllipticalActivity()) }
			CardioActivityType.ROWING_MACHINE -> { addActivity(RowingMachineActivity()) }
			CardioActivityType.SWIMMING -> { addActivity(SwimmingActivity()) }
			CardioActivityType.STAIR_CLIMBING -> { addActivity(StairClimbingActivity()) }
			else -> {}
		}
	}

	fun addActivities(activities: List<CardioActivity>) {
		for (activity in activities) {
			addActivity(activity)
		}
	}

	fun multiply(activityType: CardioActivityType, count: Int) {
		if (count <= 1 || !hasType(activityType)) { return }

		val curActivities = this.activities[activityType]!!.toList()
		for (index in 1 until count) {
			curActivities.forEach {
				addActivity(it.copy())
			}
		}
	}

	fun deleteActivity(activityType: CardioActivityType, activity: CardioActivity) {
		activities[activityType]?.remove(activity)
	}
}