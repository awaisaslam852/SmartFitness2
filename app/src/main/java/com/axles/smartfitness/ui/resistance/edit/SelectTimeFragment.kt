package com.axles.smartfitness.ui.resistance.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.view.PickerValueFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.select_time_fragment.view.*

class SelectTimeFragment(
	private var duration: Int = 0,
	val onSave: (duration: Int) -> Unit
) : BottomSheetDialogFragment() {

	lateinit var rootView: View
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		super.onCreateView(inflater, container, savedInstanceState)
		rootView = inflater.inflate(R.layout.select_time_fragment, container, false)

		init()

		return rootView
	}

	private fun init() {
		rootView.cancelBtn.setOnClickListener { onCancel() }
		rootView.saveBtn.setOnClickListener { onSave() }

		val minutes = PickerValueFactory.createFirstTimeValues()
		val seconds = PickerValueFactory.createFirstTimeValues()

		with(rootView.minutePicker) {
			minValue = 0
			maxValue = minutes.size - 1
			displayedValues = minutes.toTypedArray()

			setOnValueChangedListener {_, _, _ -> onChange() }
		}

		with(rootView.secondPicker){
			minValue = 0
			maxValue = seconds.size - 1
			displayedValues = seconds.toTypedArray()

			setOnValueChangedListener {_, _, _ -> onChange() }
		}

		update()
	}

	private fun update() {
		val min = duration / 60
		val sec = duration % 60

		rootView.minutePicker.value = min
		rootView.secondPicker.value = sec
	}

	private fun onSave() {
		onSave.invoke(duration)
		dismiss()
	}

	private fun onCancel() {
		dismiss()
	}

	private fun onChange() {
		val minute = rootView.minutePicker.value
		val second = rootView.secondPicker.value

		duration = minute * 60 + second
	}
}