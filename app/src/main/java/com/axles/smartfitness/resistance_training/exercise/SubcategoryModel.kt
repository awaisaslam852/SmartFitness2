package com.axles.smartfitness.resistance_training.exercise

import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseManager
import com.axles.smartfitness.engine.resistance.exercise.Exercise.*
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.exercise.ExerciseSubCategory
import com.axles.smartfitness.ui.resistance.viewModel.ExerciseListItemViewModel

data class SubcategoryModel(
	val subCategory: ExerciseSubCategory,
	var exerciseListModels: List<ExerciseListItemViewModel>,
	var isExpanded: Boolean = false
) {
	companion object {
		fun create(muscleGroup: MuscleGroup, category: ExerciseCategory, subCategory: ExerciseSubCategory): SubcategoryModel {
			return SubcategoryModel(subCategory, ExerciseManager.exercises(muscleGroup, category, subCategory).map {
				ExerciseListItemViewModel(
					it
				)
			})
		}
	}

	fun setExercise(exercises: List<Exercise>) {
		exerciseListModels = exercises.map {
			ExerciseListItemViewModel(
				it
			)
		}
	}
}