package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.logD
import kotlinx.android.synthetic.main.double_value_picker.view.*

class TwoPickersPopupWindow(private val view: View,
                            private val firstPickerValues: List<String>,
                            private val separatorCharacter: String,
                            private val secondPickerValues: List<String>,
                            private val onPick: (first: String, second: String) -> Unit) {

    private val popupView : View by lazy {
        val popupViewInflater: LayoutInflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupViewInflater.inflate(R.layout.double_value_picker, null)
    }
    private val popupWindow: PopupWindow by lazy {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        PopupWindow(popupView, view.measuredWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true)//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
    }

    init {
        setData()
        setListeners()
    }

    var currentFirst = "0"
    var currentSecond = "0"
    private var currentIndexFirst = 0
    private var currentIndexSecond = 0

    fun showPopupMenu() {
        restoreIndices()

        popupWindow.showAsDropDown(view, 0,0)//view.layoutParams.width.pxToDp(), 0)//(-164).pxToDp(), (-100).pxToDp())
        logD("view width on show popup popup view width ${popupView.layoutParams.width}")
    }

    private fun restoreIndices() {
        popupView.numberPickerMinute.value = currentIndexFirst
        popupView.numberPickerSeconds.value = currentIndexSecond
    }

    private fun setData() {
        val first = popupView.numberPickerMinute
        first.minValue = 0
        first.maxValue = firstPickerValues.size-1
        first.displayedValues = firstPickerValues.toTypedArray()
        first.setDividerThickness(1)
        first.setDividerDistance(34.dpToPx())
        first.isFadingEdgeEnabled = true

        first.setTextColorResource(R.color.black)
        first.setOnValueChangedListener { _, _, newVal ->
            currentIndexFirst = newVal
            currentFirst = firstPickerValues[newVal]
            onPick(currentFirst, currentSecond)
        }

        val separator = popupView.numberPickerSeparator
        separator.minValue = 0
        separator.maxValue = 0
        separator.setDividerThickness(1)
        separator.setDividerDistance(34.dpToPx())
        separator.isFadingEdgeEnabled = true
        separator.displayedValues = arrayOf(separatorCharacter)

        val second = popupView.numberPickerSeconds
        second.minValue = 0
        second.maxValue = secondPickerValues.size-1
        second.displayedValues = secondPickerValues.toTypedArray()
        second.setDividerThickness(1)
        second.setDividerDistance(34.dpToPx())
        second.isFadingEdgeEnabled = true
        second.setOnValueChangedListener { _, _, newVal ->
            currentIndexSecond = newVal
            currentSecond = secondPickerValues[newVal]
            onPick(currentFirst, currentSecond)
        }

    }

    fun restorePickerState(firstTargetItem: String, secondTargetItem: String){
        val indexFirst = firstPickerValues.indexOf(firstTargetItem)
        if (indexFirst > 0){
            currentIndexFirst = indexFirst
            currentFirst = firstPickerValues[indexFirst]
            popupView.numberPickerMinute.value = indexFirst
        }
        val indexSecond = secondPickerValues.indexOf(secondTargetItem)
        if (indexSecond > 0){
            currentIndexSecond = indexSecond
            currentSecond = secondPickerValues[indexSecond]
            popupView.numberPickerSeconds.value = indexSecond
        }
    }

    private fun setListeners() {
        setClosePopupListeners()
    }

    private fun setClosePopupListeners() {
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            return@setOnTouchListener true
        }
    }
}
