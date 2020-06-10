package com.axles.smartfitness.engine.resistance.set

import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.core.WorkoutMethod

abstract class ResistanceSet {
	companion object {
		fun default(): DefaultSet {
			return DefaultSet(10.toString(), mutableListOf(15.0))
		}

		fun sameSuperSet(exercises: List<Exercise>): SameSuperSet {
			val result = SameSuperSet()
			result.addSuperSetItems(exercises)
			return result
		}

		fun differentSuperSet(exercises: List<Exercise>): DifferentSuperSet {
			val result = DifferentSuperSet()
			result.addSuperSetItems(exercises)
			return result
		}

		fun dropSet(dropSets: MutableList<String>, kgs: MutableList<Double>): DropSet {
			return DropSet(dropSets, mutableListOf(kgs))
		}

		fun percent(rm: Int, kg: Double, percent: Int): PercentSet {
			return PercentSet(rm.toString(), mutableListOf(kg), percent)
		}

		fun pyramid(reps: Int, kg: Double): PyramidSet {
			return PyramidSet(reps.toString(), mutableListOf(kg))
		}

		fun defaultSet(): Int {
			return 3
		}

		fun setsRange(): List<Int> {
			var result = (1..99).toList()
			return result
		}

		fun defaultRep(): Int {
			return 10
		}

		fun repsRange(): List<String> {
			var result = (1..99).toList().map { it.toString() }.toMutableList()
			result.add(0, "Max")
			return result
		}

		fun defaultKg(): Double {
			return 15.0
		}

		fun placeHolderKg(): Double = -1.0
		fun isPlaceHolderKg(value: Double) = value == placeHolderKg()
		fun kgsBigRange(): List<Int> {
			var result = (0..300).toList()
			return result
		}

		fun kgsSmallRange(): List<String> {
			return listOf("0", ".25", ".5", ".75")
		}

		fun percentRange(): List<String> {
			var result = (0..150 step 5).toList().map { "$it%" }
			return result
		}

		fun minuteRange(): List<String> {
			return (0..59).map { String.format("%02d", it) }
		}

		fun secondsRange(): List<String> {
			return listOf("00", "10", "20", "30", "40", "50")
		}
	}

	fun isSuperSet(): Boolean {
		val workoutMethod = workoutMethod()
		return (workoutMethod == WorkoutMethod.SAME_MUSCLE_SUPERSET || workoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET)
	}

	open fun workoutMethod(): WorkoutMethod {
		return when (this) {
			is SameSuperSet -> WorkoutMethod.SAME_MUSCLE_SUPERSET
			is DifferentSuperSet -> WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET
			is DropSet -> WorkoutMethod.DROP_SET
			is PercentSet -> WorkoutMethod.PERCENT
			is PyramidSet -> WorkoutMethod.PYRAMID
			else -> WorkoutMethod.DEFAULT
		}
	}

	fun valueCount(): Int {
		if (this is SuperSet) {
			return reps.size
		}
		if (this is DropSet) {
			return reps.size
		}
		return 1
	}

	open fun addValue() {
		if (this is DropSet) {
			addValue()
			return
		}
	}

	open fun reduceValue() {
		if (this is DropSet) {
			reduceValue()
			return
		}
	}

	fun addSuperSetItems(superSetItems: List<Exercise>) {
		if (this is SuperSet) {
			addExercises(superSetItems)
		}
	}

	var id: Int = 0
	var set: Int = 3
	var restTime: Int = 45
	var indexOfKg: Int = 0
	fun resetKgIndex() { indexOfKg = 0 }
	open fun kgCount(): Int { return  1 }
	open fun addKg() {}
	open fun kgToString(): String { return "" }
	abstract fun copy(): ResistanceSet
}