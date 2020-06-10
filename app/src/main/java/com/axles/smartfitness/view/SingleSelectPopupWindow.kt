package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import kotlinx.android.synthetic.main.single_selection_popup.view.*

class SingleSelectPopupWindow(val view : View, val title: String, private val onSelect:(value: String)->Unit) {
	private val container : View by lazy {
		val popupViewInflater: LayoutInflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		popupViewInflater.inflate(R.layout.single_selection_popup, null)
	}

	private val popupWindow: PopupWindow by lazy {
		PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
	}

	init {
		container.topBarTitleView.setOnClickListener {
			onSelect.invoke(title)
			popupWindow.dismiss()
		}

		container.setOnTouchListener { v, event ->
			popupWindow.dismiss()
			return@setOnTouchListener true
		}
	}

	fun show() {
		container.topBarTitleView.text = title
		popupWindow.showAsDropDown(view, (-23).dpToPx(), (0).dpToPx())//(-164).pxToDp(), (-100).pxToDp())
	}
}