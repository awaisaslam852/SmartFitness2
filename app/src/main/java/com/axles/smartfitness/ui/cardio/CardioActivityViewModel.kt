package com.axles.smartfitness.ui.cardio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.extensions.logD

class CardioActivityViewModel : ViewModel(){
    val cardioActivityToEdit = MutableLiveData<CardioActivity>()
    val editedCardioActivity = MutableLiveData<CardioActivity>()

    fun saveCardioActivityToEdit(cardioActivity: CardioActivity){
        logD("saving exercise to edit")
        cardioActivityToEdit.value = cardioActivity
    }

    fun saveEditedCardioActivity(cardioActivity: CardioActivity){
        editedCardioActivity.value = cardioActivity
    }

}