package com.axles.smartfitness.ui.circuit.adapter.viewHoler

import android.view.MotionEvent
import android.view.View
import com.axles.smartfitness.engine.cardio.StairClimbingActivity
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundItemViewModel
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter
import kotlinx.android.synthetic.main.circuit_stair_climbing_activity_list_item.view.*

class CardioStairClimbingViewHolder(
	view: View,
	val adapter: CircuitRoundListAdapter
): CircuitRoundItemListViewHolder(view) {
	private lateinit var activity: StairClimbingActivity
	override fun bind(model: CircuitRoundItemViewModel) {
		activity = model.item.cardioActivity as StairClimbingActivity
		super.bind(model)
	}

	override fun init() {
		super.init()
		with (itemView) {
			dragBtn.setOnTouchListener { v, event ->
				when (event.action) {
					MotionEvent.ACTION_DOWN -> { adapter.requestDrag(this@CardioStairClimbingViewHolder) }
					MotionEvent.ACTION_UP -> { v.performClick() }
					else -> {}
				}
				true
			}
			deleteBtn.setOnClickListener { adapter.onDeleteRoundItem(model) }

			timeContainer.setOnClickListener { adapter.onEdit(model) }
			resistanceContainer.setOnClickListener { adapter.onEdit(model) }
			stepContainer.setOnClickListener { adapter.onEdit(model) }
			spmContainer.setOnClickListener { adapter.onEdit(model) }
		}
	}

	override fun update() {
		super.update()
		with (itemView) {
			titleView.text = activity.type.title()
			photoView.setImageResource(activity.type.iconResId())

			resistancePickerValueView.text = activity.resistance.toString()
			stepPickerValueView.text = activity.steps.toString()
			val spm = "${activity.spmFirst} - ${activity.spmSecond}"
			spmPickerValueView.text = spm

			timePickerValueView.text = activity.timeSeconds.toTimerString()

			if (adapter.isEditable) {
				editControlContainer.makeVisible()
				timePickerIconView.makeVisible()
				resistancePickerIconView.makeVisible()
				stepPickerIconView.makeVisible()
				spmPickerIconView.makeVisible()
			} else {
				editControlContainer.makeGone()
				timePickerIconView.makeGone()
				resistancePickerIconView.makeGone()
				stepPickerIconView.makeGone()
				spmPickerIconView.makeGone()
			}
		}
	}

	override fun applyDragging(isDragging: Boolean) {
		with (itemView) {
			cardHolder.cardElevation = if (isDragging) 4.dpToPx().toFloat() else 0f
		}
	}
}