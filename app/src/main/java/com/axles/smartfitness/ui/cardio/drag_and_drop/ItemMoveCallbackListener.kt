package com.axles.smartfitness.ui.cardio.drag_and_drop

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.ui.cardio.adapter_behaviour.DragAndDropBehaviour

class ItemMoveCallbackListener<T: CardioActivity>(val dragAndDropAdapter: DragAndDropBehaviour<T>) :
    ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(ItemTouchHelper.DOWN).or(ItemTouchHelper.ACTION_STATE_DRAG),
        ItemTouchHelper.START) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if(dragAndDropAdapter.canBeMoved(viewHolder.adapterPosition, target.adapterPosition)) {
            dragAndDropAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        } else return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //no swipe needed
    }
}