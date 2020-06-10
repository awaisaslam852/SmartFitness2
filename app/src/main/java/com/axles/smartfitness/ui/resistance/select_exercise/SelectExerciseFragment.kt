package com.axles.smartfitness.ui.resistance.select_exercise

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.resistance.exercise.ExerciseManager
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.resistance.search_exercise.SearchExerciseFragment
import com.axles.smartfitness.ui.resistance.viewModel.ExerciseListItemViewModel
import com.phelat.navigationresult.navigateUp
import kotlinx.android.synthetic.main.select_exercise_fragment.*
import kotlinx.android.synthetic.main.toolbar_exercise_selection.*

class SelectExerciseFragment : BaseFragment(R.layout.select_exercise_fragment) {
	private val args: SelectExerciseFragmentArgs by navArgs()

	private lateinit var program: Program
	private lateinit var muscleGroup: MuscleGroup
	private var selectedCategory: ExerciseCategory = ExerciseCategory.MACHINES
		set(value) {field = value
			Helper.showLoading(childFragmentManager)
			ExerciseManager.getExercises(muscleGroup, selectedCategory, object : ApiManager.CompletionListener {
				override fun onCompleted(error: String?) {
					Helper.hideLoading()
					if (error != null) {
						Helper.showErrorAlert(childFragmentManager, error)
						return
					}
					adapters[selectedCategory]?.update()
				}
			})
		}

	private var parentExercise: ResistanceExercise? = null

	companion object {
		const val REQUEST_CODE = 0x0515
	}

	private val adapters: Map<ExerciseCategory, SubcategoryListAdapter> by lazy {
		val map = mutableMapOf<ExerciseCategory, SubcategoryListAdapter>()
		ExerciseCategory.values().forEach {
			map[it] = SubcategoryListAdapter(
				muscleGroup,
				it,
				onExerciseDetail = ::onExerciseClicked,
				onExerciseSelected = { listItemModel, isSelected -> onExerciseSelected(listItemModel, isSelected) }
			)
		}
		return@lazy map
	}

	override fun onFragmentResult(requestCode: Int, bundle: Bundle) {
		if (requestCode == SearchExerciseFragment.REQUEST_CODE) {
			navigateUp(REQUEST_CODE, bundle)
		}
	}

	override fun resolveArguments() {
		program = args.program
		muscleGroup = args.muscleGroup
		parentExercise = args.parentExercise
	}

	override fun init() {
		categoriesRecyclerView.adapter = ExerciseCategoryListAdapter(
			onSelect = {
				selectedCategory = it
				update()
			}
		)

		doneBtn.setOnClickListener { onDone() }
		topBarSearchBtn.setOnClickListener { onSearch() }
		topBarBackBtn.setOnClickListener { onBack() }
	}

	private fun getCheckedExercises(): List<Exercise> {
		val selectedExercises = mutableListOf<Exercise>()
		adapters.keys.forEach {
			selectedExercises.addAll(adapters[it]?.selectedExercises() ?: listOf())
		}
		return selectedExercises
	}

	override fun update() {
		topBarTitleView.text = "${getString(R.string.select)} ${muscleGroup.title()} ${getString(R.string.exercises)}"
		if (program.type() == ProgramType.CIRCUIT) {
			doneBtn.makeGone()
		} else {
			doneBtn.makeVisible()
		}

		if (selectedCategory != ExerciseCategory.FAVORITE) {
			recyclerViewSubcategory.adapter = adapters[selectedCategory]
		} else {
			recyclerViewSubcategory.adapter = adapters[selectedCategory]
		}
	}

	private fun onBack() {
		findNavController().navigateUp()
	}

	private fun onExerciseSelected(listItemModel: ExerciseListItemViewModel, isSelected: Boolean) {
		if (!isSelected) { return }
		when (program.type()) {
			ProgramType.RESISTANCE -> {
				if (parentExercise != null) {
					val selectedExercises = getCheckedExercises()
					var totalSize = 1
					if (parentExercise!!.superSetItemsCount() > 0) { totalSize = parentExercise!!.superSetItemsCount() }
					totalSize += selectedExercises.size
					if (totalSize > 6) {
						Helper.showErrorToast(requireActivity(), R.string.the_limit_is_6_exercises)
						listItemModel.isSelected = false
						adapters[selectedCategory]?.notifyDataSetChanged()
					}
				}
			}
			ProgramType.CIRCUIT -> {
				val exercise = listItemModel.exercise
				val bundle = bundleOf(Constants.Bundle.Exercise to exercise)
				navigateUp(REQUEST_CODE, bundle)
			}
			else -> {}
		}
	}

	private fun onDone() {
		val selectedExercises = getCheckedExercises()
		val bundle = bundleOf("exercises" to selectedExercises)
		navigateUp(REQUEST_CODE, bundle)
	}

	private fun onSearch() {
		if (program is ResistanceProgram) {
			val action = SelectExerciseFragmentDirections.toSearchExercise(program, muscleGroup)
			findNavController().navigate(action)
			return
		}

		if (program is CircuitProgram) {
			val action = SelectExerciseFragmentDirections.toSearchExercise(program, muscleGroup)
			navigate(action, SearchExerciseFragment.REQUEST_CODE)
		}
	}

	private fun onExerciseClicked(exercise: Exercise) {
		val action = SelectExerciseFragmentDirections.toExerciseDetailFragment(exercise)
		findNavController().navigate(action)
	}
}