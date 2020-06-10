package com.axles.smartfitness.ui.resistance.exercise_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axles.smartfitness.engine.resistance.ResistanceExercise

class ExerciseViewModel : ViewModel(){

    val activeExercise : MutableLiveData<ResistanceExercise> = MutableLiveData()



}