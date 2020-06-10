package com.axles.smartfitness.engine.network.response.resistance.exercise

import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory

open class ApiExercise(
	var id: Int = 0,
	var title: String = "",
	var mainImageUrl: String? = null,
	var exerciseImages: List<Exercise.ImageUrl> = listOf(),
	var category: String = "",
	var videoUrl: String? = null,
	var descriptions: List<Exercise.Description> = listOf(),
	var favourite: Boolean = false
) {
	companion object {
		fun fromExercise(exercise: Exercise): ApiExercise {
			return ApiExercise(
				id = exercise.id,
				title = exercise.title,
				mainImageUrl = exercise.mainImageUrl,
				exerciseImages = exercise.exerciseImages,
				category = exercise.category.apiKey(),
				videoUrl = exercise.videoUrl,
				descriptions = exercise.descriptions,
				favourite = exercise.favourite
			)
		}
	}

	fun category(): ExerciseCategory {
		return ExerciseCategory.fromTitle(title)
	}

	fun toExercise(): Exercise {
		return Exercise().also {
			it.id = this.id
			it.title = this.title
			it.mainImageUrl = this.mainImageUrl ?: ""
			it.exerciseImages = this.exerciseImages
			it.category = this.category()
			it.videoUrl = this.videoUrl ?: ""
			it.descriptions = this.descriptions
			it.favourite = this.favourite
		}
	}
}