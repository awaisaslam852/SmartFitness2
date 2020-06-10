package com.axles.smartfitness.engine.network.response.resistance.exercise

import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.set.DefaultSet
import com.axles.smartfitness.extensions.toSafeDouble

open class ApiResistanceExercise(
	var programExerciseId: Int = 0,
	var privateId: String = "",
	var setsValue: Int = 0,
	var machineNumber: Int = 0,
	var notes: String = "",
	var repsValue: Int = 0,
	var repsTime: String? = null,
	var indexOfKg: Int = 0,
	private val kgValue: Any = listOf<Any>(),
	private val kgValues: List<Double> = listOf(),
	var restTime: String = "00:00",
	var selected: Boolean = false,
	var expanded: Boolean = false
): ApiExercise() {
	companion object {
		fun fromResistanceExercise(resistanceExercise: ResistanceExercise): ApiResistanceExercise {
			return ApiResistanceExercise().also {
				it.id = resistanceExercise.exercise.id
				it.title = resistanceExercise.exercise.title
				it.mainImageUrl = resistanceExercise.exercise.mainImageUrl
				it.exerciseImages = resistanceExercise.exercise.exerciseImages
				it.category = resistanceExercise.exercise.category.apiKey()
				it.videoUrl = resistanceExercise.exercise.videoUrl
				it.descriptions = resistanceExercise.exercise.descriptions
				it.favourite = resistanceExercise.exercise.favourite
			}
		}

		private fun fromDefaultSet(resistanceExercise: ResistanceExercise): ApiResistanceExercise {
			return fromResistanceExercise(resistanceExercise).also {

			}
		}
	}
	private fun kgValues(): MutableList<Double> {
		if (!kgValues.isNullOrEmpty()) { return kgValues.toMutableList() }
		if (kgValue is List<*>) { return (kgValue as List<Any>).map { it.toString().toSafeDouble() }.toMutableList() }
		return mutableListOf()
	}

	fun toDefaultSet(): DefaultSet {
		return DefaultSet(
			rep = (if (repsTime.isNullOrEmpty()) repsValue.toString() else repsTime) ?: "0",
			kg = kgValues()
		).also {
			it.id = this.id
			it.set = this.setsValue
		}
	}

	fun toResistanceExercise(): ResistanceExercise? {
		val exercise = toExercise()
		return ResistanceExercise(exercise).also {
			it.id = this.id
			it.note = this.notes
			it.machineNumber = this.machineNumber

			it.workoutMethod = WorkoutMethod.DEFAULT
			it.sets.add(this.toDefaultSet())
		}
	}
}