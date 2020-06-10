package com.axles.smartfitness.resistance_training.split

import com.axles.smartfitness.engine.core.MuscleGroup

data class SplitMuscleGroupModel(val muscleGroup: MuscleGroup, var isDragging: Boolean = false)