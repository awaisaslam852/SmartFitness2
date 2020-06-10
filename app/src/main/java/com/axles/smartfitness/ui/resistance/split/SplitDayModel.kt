package com.axles.smartfitness.ui.resistance.split

import com.axles.smartfitness.resistance_training.split.SplitMuscleGroupModel

data class SplitDayModel(val dayTag: String, val dayNumber: Int, val muscleTrainingsToday: MutableList<SplitMuscleGroupModel>)