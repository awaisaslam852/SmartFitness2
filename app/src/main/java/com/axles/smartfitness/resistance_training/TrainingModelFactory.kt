package com.axles.smartfitness.resistance_training

import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.core.MuscleGroup

object TrainingModelFactory {

    fun getInitialTrainingModel(): MutableMap<MuscleGroup, MutableList<ResistanceExercise>>{
        val initialMap = mutableMapOf<MuscleGroup, MutableList<ResistanceExercise>>()
        MuscleGroup.values().map {
            initialMap[it] = mutableListOf()
        }
        return initialMap
    }
}