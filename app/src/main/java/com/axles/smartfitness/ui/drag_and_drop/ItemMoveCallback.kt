package com.axles.smartfitness.ui.drag_and_drop

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperContract {
	fun onRowMoved(fromPosition: Int, toPosition: Int)
	fun onRowSelected(viewHolder: RecyclerView.ViewHolder?)
	fun onRowClear(viewHolder: RecyclerView.ViewHolder?)
}

interface StartDragListener {
	fun requestDrag(viewHolder: RecyclerView.ViewHolder)
}

class ItemMoveCallback(val adapter: ItemTouchHelperContract): ItemTouchHelper.Callback() {
	override fun isLongPressDragEnabled() = false
	override fun isItemViewSwipeEnabled() = false


	override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
		val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
		return makeMovementFlags(dragFlags, 0)
	}

	override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
		adapter.onRowMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition);
		return true;
	}

	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

	override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
		if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
			adapter.onRowSelected(viewHolder)
		}

		super.onSelectedChanged(viewHolder, actionState)
	}

	override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
		super.clearView(recyclerView, viewHolder)
		adapter.onRowClear(viewHolder)
	}
}