package com.axles.smartfitness.ui.dialogs

import android.view.View
import android.widget.ImageView
import com.axles.smartfitness.R
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.ui.base.BaseDialogFragment

class ChooseLanguageDialog: BaseDialogFragment(R.layout.choose_language_dialog) {

	private val localizationManager = LocalizationSettingsManager

	override fun init(rootView: View) {
		super.init(rootView)

		with (rootView) {
			val dismissButton = findViewById<ImageView>(R.id.closeBtn)
			dismissButton.setOnClickListener {
				dismiss()
			}

			val englishBtn = findViewById<View>(R.id.englishBtn)
			val englishCheckbox = findViewById<ImageView>(R.id.englishCheckbox)
			val hebrewBtn = findViewById<View>(R.id.hebrewBtn)
			val hebrewCheckbox = findViewById<ImageView>(R.id.hebrewCheckbox)

			if (localizationManager.isIw()) {
				englishCheckbox.setImageResource(R.drawable.check_box_normal_yellow)
				hebrewCheckbox.setImageResource(R.drawable.check_box_checked_yellow)
			}

			englishBtn.setOnClickListener { onEnglish()	}
			hebrewBtn.setOnClickListener { onHebrew() }
		}
	}

	private fun onEnglish() {
		localizationManager.setEn()
		requireActivity().recreate()
		dismiss()
	}

	private fun onHebrew() {
		localizationManager.setIw()
		requireActivity().recreate()
		dismiss()
	}
}