package com.axles.smartfitness.resistance_training

import com.axles.smartfitness.engine.core.MuscleGroup

data class ResistanceMuscleGroupViewModel(val muscleGroup: MuscleGroup) {
	var isDone = false
	var day: String = ""
}