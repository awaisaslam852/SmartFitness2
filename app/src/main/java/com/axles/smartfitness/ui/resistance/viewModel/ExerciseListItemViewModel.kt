package com.axles.smartfitness.ui.resistance.viewModel

import com.axles.smartfitness.engine.resistance.exercise.Exercise

data class ExerciseListItemViewModel(
	val exercise: Exercise,
	var isSelected: Boolean = false
)