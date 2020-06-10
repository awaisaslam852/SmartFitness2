package com.axles.smartfitness.ui.resistance.viewModel

import com.axles.smartfitness.engine.resistance.ResistanceExercise

class ResistanceExerciseListItemViewModel(
	val resistanceExercise: ResistanceExercise,
	var isExpanded: Boolean = false
)