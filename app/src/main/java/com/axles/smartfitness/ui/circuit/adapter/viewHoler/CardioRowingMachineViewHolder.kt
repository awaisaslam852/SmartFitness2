package com.axles.smartfitness.ui.circuit.adapter.viewHoler

import android.view.MotionEvent
import android.view.View
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.RowingMachineActivity
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundItemViewModel
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter
import kotlinx.android.synthetic.main.circuit_rowing_machine_activity_list_item.view.*
import java.util.*

class CardioRowingMachineViewHolder(
	view: View,
	val adapter: CircuitRoundListAdapter
): CircuitRoundItemListViewHolder(view) {
	private lateinit var activity: RowingMachineActivity
	override fun bind(model: CircuitRoundItemViewModel) {
		activity = model.item.cardioActivity as RowingMachineActivity
		super.bind(model)
	}

	override fun init() {
		with (itemView) {
			dragBtn.setOnTouchListener { v, event ->
				when (event.action) {
					MotionEvent.ACTION_DOWN -> { adapter.requestDrag(this@CardioRowingMachineViewHolder) }
					MotionEvent.ACTION_UP -> { v.performClick() }
					else -> {}
				}
				true
			}
			deleteBtn.setOnClickListener { adapter.onDeleteRoundItem(model) }

			timeContainer.setOnClickListener { adapter.onEdit(model) }
			distanceContainer.setOnClickListener { adapter.onEdit(model) }
			intensityContainer.setOnClickListener { adapter.onEdit(model) }
			paceContainer.setOnClickListener { adapter.onEdit(model) }
			spmContainer.setOnClickListener { adapter.onEdit(model) }
		}
		update()
	}

	override fun update() {
		super.update()
		with(itemView) {
			titleView.text = activity.type.title()
			photoView.setImageResource(activity.type.iconResId())

			intensityPickerValueView.text = itemView.context.getString(activity.intensity.intensityRes)
			pacePickerValueView.text = activity.paceSeconds.toTimerString()
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
					distancePickerValueView.text = String.format(Locale.UK,"%.2f", activity.distance)
				}
				else -> {}
			}

			if (adapter.isEditable) {
				editControlContainer.makeVisible()
				timePickerIconView.makeVisible()
//				timeDropDownIcon.makeVisible()
				distancePickerIconView.makeVisible()
				distanceDropDownIcon.makeVisible()
				intensityPickerIconView.makeVisible()
				pacePickerIconView.makeVisible()
				spmPickerIconView.makeVisible()
			} else {
				editControlContainer.makeGone()
				timePickerIconView.makeGone()
//				timeDropDownIcon.makeGone()
				distancePickerIconView.makeGone()
				distanceDropDownIcon.makeGone()
				intensityPickerIconView.makeGone()
				pacePickerIconView.makeGone()
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