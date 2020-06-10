package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.pxToDp
import kotlinx.android.synthetic.main.picker_container.view.*

class OnePickerPopupWindow(
	private val view: View,
	private val pickerValues: List<Any>,
	private val onPick: (value: Any) -> Unit) {

	private val popupView: View by lazy {
		val popupViewInflater: LayoutInflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		popupViewInflater.inflate(R.layout.picker_container, null)
	}

	private val popupWindow: PopupWindow by lazy {
		view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
		PopupWindow(popupView, view.measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)
//        PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
	}

	init {
		setData()
		setListeners()
	}

	private var currentIndex = 0

	fun showPopupMenu() {
		restoreIndices()
		popupWindow.showAsDropDown(
			view,
			view.layoutParams.width.pxToDp(),
			0
		)//(-164).pxToDp(), (-100).pxToDp())
	}

	private fun restoreIndices() {
		popupView.pickerView.value = currentIndex
	}

	private fun setData() {
		val picker = popupView.pickerView
		picker.minValue = 0
		picker.maxValue = pickerValues.size - 1
		picker.displayedValues = pickerValues.map { it.toString() }.toTypedArray()
		picker.setDividerThickness(1)
		picker.setDividerDistance(34.dpToPx())

		picker.smoothScroll(true, 20)
		picker.isScrollerEnabled = true


		picker.isFadingEdgeEnabled = true
		picker.setTextColorResource(R.color.black)
		picker.setOnValueChangedListener { _, _, index ->
			currentIndex = index
			var value = pickerValues[index].toString()
			if (value.contains("%")) { value = value.replace("%", "") }
			onPick(value)
		}
	}

	fun restorePickerState(lastValue: Any) {
		val index = popupView.pickerView.displayedValues.indexOf(lastValue.toString())
		if (index > 0) {
			currentIndex = index
			popupView.pickerView.value = index
		}
	}

	private fun setListeners() {
		setClosePopupListeners()
	}

	private fun setClosePopupListeners() {
		popupView.setOnClickListener {
			popupWindow.dismiss()
		}
	}
}
