package com.axles.smartfitness.ui.circuit.adapter.viewHoler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundItemViewModel

open class CircuitRoundItemListViewHolder(
	view: View
): RecyclerView.ViewHolder(view) {
	lateinit var model: CircuitRoundItemViewModel

	open fun bind(model: CircuitRoundItemViewModel) {
		this.model = model
		init()
	}

	open fun init() {
		update()
	}

	open fun update() {}

	open fun applyDragging(isDragging: Boolean) {}
}