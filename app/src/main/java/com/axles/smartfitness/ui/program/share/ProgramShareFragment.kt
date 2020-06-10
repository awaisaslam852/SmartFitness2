package com.axles.smartfitness.ui.program.share

import android.text.Editable
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.program_share_fragment.*

class ProgramShareFragment: BaseFragment(R.layout.program_share_fragment) {
	val args: ProgramShareFragmentArgs by navArgs()
	private lateinit var program: Program

	private var searchText = ""
		set(value) {
			field = value
			clearSearchBtn.makeVisibleOrGone(searchText.isNotEmpty())
			performSearch()
		}

	override fun resolveArguments() {
		program = args.program
	}

	override fun init() {
		topBarBackBtn.setOnClickListener { findNavController().navigateUp() }
		topBarShareExternalBtn.setOnClickListener { onShareExternal() }
		clearSearchBtn.setOnClickListener { searchText = "" }
		editTextSearch.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				if (s != null) {
					for (span in s.getSpans(0, s.length, UnderlineSpan::class.java)) {
						s.removeSpan(span)
					}
				}
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
				searchText = text?.toString() ?: ""
			}
		})
	}

	override fun update() {

	}

	private fun performSearch() {

	}

	private fun onShareExternal() {

	}
}