package com.axles.smartfitness.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.axles.smartfitness.R
import kotlinx.android.synthetic.main.choose_add_cardio_program_dialog.view.*

class ChooseAddCardioProgramDialog(val onChoose: (Boolean) -> Unit): DialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return requireActivity().let {
			val builder = AlertDialog.Builder(it)
			val view = View.inflate(context, R.layout.choose_add_cardio_program_dialog, null)

			with (view) {
				closeBtn.setOnClickListener { dismiss() }
				addBtn.setOnClickListener {
					onChoose.invoke(true)
					dismiss()
				}

				noBtn.setOnClickListener {
					onChoose.invoke(false)
					dismiss()
				}
			}

			builder.setView(view)
			builder.create()
		} ?: throw IllegalStateException("Activity cannot be null")
	}
}