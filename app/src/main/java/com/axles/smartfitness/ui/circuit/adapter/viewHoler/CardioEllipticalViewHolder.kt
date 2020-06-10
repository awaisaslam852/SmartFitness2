package com.axles.smartfitness.ui.circuit.adapter.viewHoler

import android.view.MotionEvent
import android.view.View
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.EllipticalActivity
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundItemViewModel
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter
import kotlinx.android.synthetic.main.circuit_elliptical_activity_list_item.view.*
import java.util.*

class CardioEllipticalViewHolder(
	view: View,
	val adapter: CircuitRoundListAdapter
): CircuitRoundItemListViewHolder(view) {
	private lateinit var activity: EllipticalActivity
	override fun bind(model: CircuitRoundItemViewModel) {
		activity = model.item.cardioActivity as EllipticalActivity
		super.bind(model)
	}

	override fun init() {
		with (itemView ) {
			dragBtn.setOnTouchListener { v, event ->
				when (event.action) {
					MotionEvent.ACTION_DOWN -> { adapter.requestDrag(this@CardioEllipticalViewHolder) }
					MotionEvent.ACTION_UP -> { v.performClick() }
					else -> {}
				}
				true
			}
			deleteBtn.setOnClickListener { adapter.onDeleteRoundItem(model) }

			timeContainer.setOnClickListener { adapter.onEdit(model) }
			distanceContainer.setOnClickListener { adapter.onEdit(model) }
			resistanceContainer.setOnClickListener { adapter.onEdit(model) }
			inclineContainer.setOnClickListener { adapter.onEdit(model) }
			spmContainer.setOnClickListener { adapter.onEdit(model) }
		}
		super.init()
	}

	override fun update() {
		super.update()
		with (itemView) {
			titleView.text = activity.type.title()
			photoView.setImageResource(activity.type.iconResId())

			resistancePickerValueView.text = activity.resistance.toString()
			inclinePickerValueView.text = String.format(Locale.UK,"%.1f", activity.incline)
			val spm = "${activity.spmFirst} - ${activity.spmSecond}"
			spmPickerValueView.text = spm

			when(activity.valueType){
				CardioActivity.ValueType.TIME -> {
					timeContainer.makeVisible()
					distanceContainer.makeGone()
					timePickerValueView.text = activity.timeSeconds.toTimerString()
				}

				CardioActivity.ValueType.DISTANCE -> {
					timeContainer.makeGone()
					distanceContainer.makeVisible()
					distancePickerValueView.text = activity.timeSeconds.toTimerString()
				}
				else -> {}
			}

			if (adapter.isEditable) {
				editControlContainer.makeVisible()
				timePickerIconView.makeVisible()
//				timeDropDownIcon.makeVisible()
				distancePickerIconView.makeVisible()
				distanceDropDownIcon.makeVisible()
				resistancePickerIconView.makeVisible()
				inclinePickerIconView.makeVisible()
				spmPickerIconView.makeVisible()
			} else {
				editControlContainer.makeGone()
				timePickerIconView.makeGone()
//				timeDropDownIcon.makeGone()
				distancePickerIconView.makeGone()
				distanceDropDownIcon.makeGone()
				resistancePickerIconView.makeGone()
				inclinePickerIconView.makeGone()
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