package com.axles.smartfitness.ui.resistance.search_exercise

import android.text.Editable
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseManager
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.base.BaseFragment
import com.phelat.navigationresult.navigateUp
import kotlinx.android.synthetic.main.exercise_search_fragment.*
import kotlinx.android.synthetic.main.toolbar_exercise_search.*
import kotlin.math.abs


class SearchExerciseFragment : BaseFragment(R.layout.exercise_search_fragment) {
	companion object {
		const val REQUEST_CODE = 0x0601
	}
	private val args: SearchExerciseFragmentArgs? by navArgs()

	private lateinit var program: Program
	private lateinit var muscleGroup : MuscleGroup

	private val emptySearchText by lazy {
		getString(R.string.add_your_own_exercise)
	}

	private var searchText = ""
		set(value) {
			field = value
			if (searchText.isEmpty()) {
				textViewSearchInfo.text = emptySearchText
			} else {
				textViewSearchInfo.text = searchText
			}
			clearSearchBtn.makeVisibleOrGone(searchText.isNotEmpty())
			performSearch()
		}

	override fun resolveArguments() {
		if (args == null) { return }
		program = args!!.program
		muscleGroup = args!!.muscleGroup
	}

	override fun init() {
		recyclerViewSearchExercises.adapter = SearchExerciseAdapter { clickedExercise ->
			onExerciseSelected(clickedExercise)
		}
		recyclerViewSearchExercises.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
			if (abs(scrollY-oldScrollY) > 10) {
				Helper.hideSoftKeyboard(requireActivity())
			}
		}

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

		clearSearchBtn.setOnClickListener {
			editTextSearch.setText("")
		}

		topBarBackBtn.setOnClickListener { onBack() }

		textViewSearchInfo.setOnClickListener {
			onAddOwnExercise()
		}
		imageViewAdd.setOnClickListener {
			onAddOwnExercise()
		}

		editTextSearch.requestFocus()
		Helper.showSoftKeyboard(requireActivity(), editTextSearch)
	}

	override fun update() {
		val muscleText = muscleGroup.title()
		val searchText = getString(R.string.search_s_exercises).replace("{muscleType}", muscleText)
		editTextSearch.hint = searchText
	}

	private fun onBack() {
		Helper.hideSoftKeyboard(requireActivity())
		findNavController().navigateUp()
	}

	private fun onAddOwnExercise() {
		if (textViewSearchInfo.text == emptySearchText) {
			Toast.makeText(requireContext(), R.string.please_write_your_exercise, Toast.LENGTH_LONG).show()
			return
		}

		val exercise = Exercise.createOwnExercise(muscleGroup, textViewSearchInfo.text.toString())
		onExerciseSelected(exercise)
	}

	private fun onExerciseSelected(exercise: Exercise) {
		if (program is ResistanceProgram) {
			val program = this.program as ResistanceProgram
			program.addExercise(muscleGroup, exercise)
		}

		if (program is CircuitProgram) {
			val bundle = bundleOf(Constants.Bundle.Exercise to exercise)
			navigateUp(REQUEST_CODE, bundle)
		}

		Helper.showToast(requireActivity(), R.string.exercise_is_added_to_list)
	}

	private fun performSearch() {
		val foundExercises = ExerciseManager.search(muscleGroup, searchText)
		(recyclerViewSearchExercises.adapter as SearchExerciseAdapter).setExercises(foundExercises)
	}
}