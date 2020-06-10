package com.axles.smartfitness.engine.network.response.resistance.exercise

import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.network.response.resistance.set.ApiSuperSet
import com.axles.smartfitness.engine.network.response.resistance.workoutMethod.ApiDropSetMethod
import com.axles.smartfitness.engine.network.response.resistance.workoutMethod.ApiPercentMethod
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import java.util.*

class ApiResistanceTraining(
	private val id: Int,
	private val method: String,
	private val exercise: ApiResistanceExercise,
	private val percentMethodData: ApiPercentMethod,
	private val dropSetMethodData: ApiDropSetMethod,
	private val supersetMethodData: List<ApiSuperSet> = listOf(),
	private val exercisesForSuperset: List<ApiResistanceExercise> = listOf()
) {
	fun workoutMethod(): WorkoutMethod? {
		return WorkoutMethod.values().firstOrNull { it.apiKey().toLowerCase(Locale.US) == method.toLowerCase(Locale.US) }
	}

	fun toResistanceExercise(): ResistanceExercise? {
		val exercise = exercise.toExercise() ?: return null
		return ResistanceExercise(exercise).also {
			it.id = this.id
			it.note = this.exercise.notes
			it.machineNumber = this.exercise.machineNumber

			it.workoutMethod = workoutMethod() ?: return null
			when (it.workoutMethod) {
				WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
					val exercises = exercisesForSuperset.map { response -> response.toExercise() }
					supersetMethodData.forEach { response ->
						it.sets.add(response.toSameSuperSet(exercises))
					}
				}
				WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
					val exercises = exercisesForSuperset.map { response -> response.toExercise() }
					supersetMethodData.forEach { response ->
						it.sets.add(response.toDifferentSuperSet(exercises))
					}
				}
				WorkoutMethod.DROP_SET -> {
					dropSetMethodData.sets.forEach { response ->
						it.sets.add(response.toDropSet())
					}
				}
				WorkoutMethod.PERCENT -> {
					percentMethodData.sets.forEach { response ->
						it.sets.add(response.toPercentSet())
					}
				}
				WorkoutMethod.PYRAMID -> {
					percentMethodData.sets.forEach { response ->
						it.sets.add(response.toPyramidSet())
					}
				}
				else -> {
					it.sets.add(this.exercise.toDefaultSet())
				}
			}
		}
	}
}