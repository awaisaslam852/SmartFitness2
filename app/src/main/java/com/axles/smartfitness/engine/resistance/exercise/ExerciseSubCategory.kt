package com.axles.smartfitness.engine.resistance.exercise

import com.axles.smartfitness.engine.core.MuscleGroup

class ExerciseSubCategory(
	val id: Int,
	val muscleGroup: MuscleGroup,
	val category: ExerciseCategory,
	val title: String,
	val imageUrl: String = "",
	val exercises: List<Exercise> = listOf()
)