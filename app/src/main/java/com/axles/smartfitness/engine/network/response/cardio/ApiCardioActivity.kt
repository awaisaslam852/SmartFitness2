package com.axles.smartfitness.engine.network.response.cardio

import com.axles.smartfitness.engine.cardio.*
import com.axles.smartfitness.extensions.firstInt
import com.axles.smartfitness.extensions.secondInt
import com.axles.smartfitness.extensions.toSafeDouble
import com.axles.smartfitness.extensions.toSecond

class ApiCardioActivity(
	val id: Int = 0,
	val activityType: String = "running",
	val timeValue: String = "02:00",
	val distanceValue: String = "",
	val speedValue: String =  "0.0",
	val inclineValue: String = "0.0",
	val imageName: String = "ic_clock",
	val paceValue: String = "00:00"
) {
	fun activityType(): CardioActivityType? {
		return CardioActivityType.fromApiKey(activityType)
	}

	fun valueType(): CardioActivity.ValueType? {
		return when (imageName) {
			"ic_clock" -> CardioActivity.ValueType.TIME
			"ic_distance" -> CardioActivity.ValueType.DISTANCE
			else -> CardioActivity.ValueType.TIME
		}
	}

	fun toActivity(): CardioActivity? {
		val activityType = activityType() ?: return null
		return when (activityType) {
			CardioActivityType.RUNNING -> toRunning()
			CardioActivityType.CYCLING -> toCycling()
			CardioActivityType.ELLIPTICAL -> toElliptical()
			CardioActivityType.ROWING_MACHINE -> toRowingMachine()
			CardioActivityType.SWIMMING -> toSwimming()
			CardioActivityType.STAIR_CLIMBING -> toStairClimbing()
			CardioActivityType.JUMPING_ROPE -> toJumpingRope()
		}
	}

	private fun toRunning(): RunningActivity? {
		val valueType = valueType() ?: return null
		return RunningActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()
			it.speed = this.speedValue.toSafeDouble()
			it.incline = this.inclineValue.toSafeDouble()
			it.paceSeconds = this.paceValue.toSecond()
		}
	}

	private fun toCycling(): CyclingActivity? {
		val valueType = valueType() ?: return null
		return CyclingActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()
			it.resistance = this.speedValue.toInt()
			it.rpmFirst = this.inclineValue.firstInt()
			it.rpmSecond = this.inclineValue.secondInt()
		}
	}

	private fun toElliptical(): EllipticalActivity? {
		val valueType = valueType() ?: return null
		return EllipticalActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()
			it.resistance = this.speedValue.toInt()
			it.incline = this.inclineValue.toSafeDouble()
			it.spmFirst = this.paceValue.firstInt()
			it.spmSecond = this.paceValue.secondInt()
		}
	}

	private fun toRowingMachine(): RowingMachineActivity? {
		val valueType = valueType() ?: return null
		return RowingMachineActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()

			val intensity = RowingMachineActivity.Intensity.values().firstOrNull { intensity -> intensity.apiKey() == this.speedValue } ?: RowingMachineActivity.Intensity.MEDIUM
			it.intensity = intensity
			it.paceSeconds = this.inclineValue.toSecond()
			it.spmFirst = this.paceValue.firstInt()
			it.spmSecond = this.paceValue.secondInt()
		}
	}

	private fun toSwimming(): SwimmingActivity? {
		val valueType = valueType() ?: return null
		return SwimmingActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()
			it.exercise = SwimmingActivity.ExerciseType.fromApiKey(this.inclineValue)
			it.intensity = SwimmingActivity.Intensity.fromApiKey(this.paceValue)
			it.style = SwimmingActivity.Style.fromApiKey(this.speedValue)
		}
	}

	private fun toStairClimbing(): StairClimbingActivity? {
		return StairClimbingActivity().also {
			it.valueType = CardioActivity.ValueType.TIME
			it.timeSeconds = this.timeValue.toSecond()
			it.steps = this.inclineValue.toInt()
			it.resistance = this.speedValue.toInt()
			it.spmFirst = this.paceValue.firstInt()
			it.spmSecond = this.paceValue.secondInt()
		}
	}

	private fun toJumpingRope(): JumpingRopeActivity? {
		val valueType = valueType() ?: return null
		return JumpingRopeActivity().also {
			it.valueType = valueType
			it.timeSeconds = this.timeValue.toSecond()
			it.distance = this.distanceValue.toSafeDouble()
			it.intensity = JumpingRopeActivity.Intensity.fromApiKey(this.speedValue)
		}
	}
}