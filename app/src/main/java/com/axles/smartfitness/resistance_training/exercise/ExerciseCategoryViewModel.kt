package com.axles.smartfitness.resistance_training.exercise

import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory

data class ExerciseCategoryViewModel(val category: ExerciseCategory, var isActive : Boolean = false)