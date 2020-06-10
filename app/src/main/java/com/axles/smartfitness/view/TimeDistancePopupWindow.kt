package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioActivity.*
import com.axles.smartfitness.extensions.dpToPx
import kotlinx.android.synthetic.main.time_distance_popup.view.*

class TimeDistancePopupWindow(val view : View, private val initialState: ValueType, private val onClickListener:()->Unit) {
	private val popupView : View by lazy {
		val popupViewInflater: LayoutInflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		popupViewInflater.inflate(R.layout.time_distance_popup, null)
	}

	private val popupWindow: PopupWindow by lazy {
		PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
	}

	fun showPopUp(){
		popupView.titleView.text = if (initialState == ValueType.TIME) view.context.getString(R.string.distance_colon) else view.context.getString(R.string.time_colon)
		popupWindow.showAsDropDown(view, (-30).dpToPx(), (-8).dpToPx())//(-164).pxToDp(), (-100).pxToDp())
		setListeners()
	}

	private fun setListeners() {
		setClosePopupListeners()
		popupView.titleView.setOnClickListener {
			onClickListener.invoke()
			popupWindow.dismiss()
		}
	}

	private fun setClosePopupListeners() {
		popupView.setOnTouchListener { v, event ->
			popupWindow.dismiss()
			return@setOnTouchListener true
		}
	}

}