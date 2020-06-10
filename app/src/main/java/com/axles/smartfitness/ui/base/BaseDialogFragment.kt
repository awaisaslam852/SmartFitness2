package com.axles.smartfitness.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment(@LayoutRes val layoutResourceId: Int): DialogFragment() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(layoutResourceId, container, false)
		init(rootView)
		return rootView
	}

	open fun init(rootView: View) {
		requireDialog().window!!.requestFeature(Window.FEATURE_NO_TITLE)
	}

	override fun onStart() {
		super.onStart()

		val dialog = dialog
		if (dialog != null) {
			dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		}
	}
}