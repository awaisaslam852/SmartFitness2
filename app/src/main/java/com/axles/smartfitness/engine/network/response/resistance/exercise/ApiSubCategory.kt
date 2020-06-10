package com.axles.smartfitness.engine.network.response.resistance.exercise

import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.exercise.ExerciseSubCategory

class ApiSubCategory(
	val id: Int = 0,
	val muscleGroup: String = "",
	val title: String = "",
	val category: String = "",
	val imageUrl: String? = null,
	val exercises: List<ApiExercise> = listOf()
) {
	fun muscleGroup() = MuscleGroup.fromApi(muscleGroup)
	fun category() = ExerciseCategory.fromApi(category)

	fun toSubCategory(): ExerciseSubCategory? {
		val muscleGroup = muscleGroup() ?: return null
		val category = category() ?: return null
		return ExerciseSubCategory(
			id = id,
			muscleGroup = muscleGroup,
			category = category,
			title = title,
			imageUrl = imageUrl ?: "",
			exercises = toExercises()
		)
	}

	private fun toExercises(): List<Exercise> {
		val results = mutableListOf<Exercise>()
		exercises.forEach { apiExercise ->
			val exercise = apiExercise.toExercise()
			results.add(exercise)
		}
		results.sortBy { it.id }
		return results
	}
}