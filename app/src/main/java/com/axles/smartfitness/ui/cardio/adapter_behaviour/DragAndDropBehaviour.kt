package com.axles.smartfitness.ui.cardio.adapter_behaviour

import com.axles.smartfitness.engine.cardio.CardioActivity


interface DragAndDropBehaviour<T : CardioActivity> {

    fun getDragAndDropModel():MutableList<T>
    fun notifyItems(fromPosition: Int, toPosition: Int)
    fun canBeMoved(fromPosition: Int, toPosition: Int):Boolean{
        return (getDragAndDropModel()[fromPosition].isEdited() && getDragAndDropModel()[toPosition].isEdited())
    }

    fun swapItems(fromPosition: Int, toPosition: Int){
        val model = getDragAndDropModel()
        if (fromPosition < toPosition){
            for (i in fromPosition until toPosition){
                model[i] = model.set(i+1, model[i])
            }
        } else {
            for (i in fromPosition..toPosition + 1){
                model[i] = model.set(i-1, model[i])
            }
        }
        notifyItems(fromPosition, toPosition)
    }
}