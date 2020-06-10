package com.axles.smartfitness.ui.dialogs

import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.ok_dialog_layout.view.*

class OkDialog(private val message: String): BaseDialogFragment(R.layout.ok_dialog_layout) {

	override fun init(rootView: View) {
		super.init(rootView)

		with (rootView) {
			textViewComingSoon.text = message
			closeBtn.setOnClickListener {
				dismiss()
			}
			buttonOk.setOnClickListener {
				dismiss()
			}
		}
	}
}