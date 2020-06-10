package com.axles.smartfitness.ui.dialogs

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.program.ProgramType
import kotlinx.android.synthetic.main.choose_training_program_dialog.view.*


class ChooseTrainingProgramDialog(
	val onSelect: (ProgramType) -> Unit
): DialogFragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val rootView = inflater.inflate(R.layout.choose_training_program_dialog, container, false)

		with (rootView) {
			context.theme.applyStyle(R.style.AlertDialogStyle, true)

			val titleText = textViewChooseProgramLabel.text
			val spannableStringBuilder = SpannableStringBuilder(titleText)
			val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(rootView.context, R.color.colorPrimary))
//			val styleSpan = RelativeSizeSpan(1.4f)
			spannableStringBuilder.setSpan(
				foregroundColorSpan,
				titleText.length - 2,
				titleText.length,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE
			)
//			spannableStringBuilder.setSpan(
//				styleSpan,
//				titleText.length - 2,
//				titleText.length,
//				Spannable.SPAN_INCLUSIVE_INCLUSIVE
//			)
			textViewChooseProgramLabel.text = spannableStringBuilder

			cancelBtn.setOnClickListener { dismiss() }
			resistanceBtn.setOnClickListener {
				onSelect.invoke(ProgramType.RESISTANCE)
				dismiss()
			}
			circuitBtn.setOnClickListener {
				onSelect.invoke(ProgramType.CIRCUIT)
				dismiss()
			}
			cardIoBtn.setOnClickListener {
				onSelect.invoke(ProgramType.CARDIO)
				dismiss()
			}
		}

		requireDialog().window!!.requestFeature(Window.FEATURE_NO_TITLE)

		return rootView
	}

	override fun onStart() {
		super.onStart()

		val dialog = dialog
		if (dialog != null) {
			dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		}
	}
}