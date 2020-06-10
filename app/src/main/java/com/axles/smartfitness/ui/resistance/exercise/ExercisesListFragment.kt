package com.axles.smartfitness.ui.resistance.exercise

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.OkDialog
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.resistance.select_exercise.SelectExerciseFragment
import com.phelat.navigationresult.navigateUp
import kotlinx.android.synthetic.main.exercise_list_fragment.*

class ExercisesListFragment : BaseFragment(R.layout.exercise_list_fragment) {
	companion object {
		const val REQUEST_CODE = 0x0603
	}

	private val args: ExercisesListFragmentArgs by navArgs()
	lateinit var program: ResistanceProgram
	lateinit var muscleGroup: MuscleGroup

	override fun onFragmentResult(requestCode: Int, bundle: Bundle) {
		super.onFragmentResult(requestCode, bundle)
		if (requestCode == CreateWorkoutMethodFragment.REQUEST_CODE) {
			if (bundle.containsKey("program")) {
				program = bundle["program"]!! as ResistanceProgram
				update()
			}
		}

		if (requestCode == SelectExerciseFragment.REQUEST_CODE) {
			if (bundle.containsKey("exercises")) {
				val exercises = (bundle["exercises"]!! as List<Exercise>)
				program.addExercises(muscleGroup, exercises.map { ResistanceExercise.create(it) })
				update()
			}
		}
	}

	override fun resolveArguments() {
		program = args.program as ResistanceProgram
		muscleGroup = args.muscleGroup
	}

	override fun init() {
		container.setOnClickListener {
			Helper.hideSoftKeyboard(requireContext())
		}
		topBarBackBtn.setOnClickListener { onCancel() }
		topBarDoneBtn.setOnClickListener { onDone() }
		buttonAddNewExercise.setOnClickListener { onAddExercises() }
		buttonAddMoreExercises.setOnClickListener { onAddExercises() }
		workoutMethodsBtn.setOnClickListener { onWorkoutMethod() }

		exercisesList.setHasFixedSize(false)
		exercisesList.adapter = ExercisesListAdapter(
			program,
			muscleGroup,
			childFragmentManager,
			onDelete = ::onDelete,
			onUnlinkSuperset = ::onUnlinkSuperSet,
			onExerciseDetail = ::onExerciseDetail
		)
	}

	override fun update() {
		if (!this::program.isInitialized || !this::muscleGroup.isInitialized) { return }

		updateTopBar()
		muscleTypeBackgroundView.setImageResource(muscleGroup.largeImageResource())

		val exercises = program.exercises(muscleGroup)
		if (exercises.isNullOrEmpty()) {
			textViewExercisesNotFound.makeVisible()
			textViewClickTheButtonToAdd.makeVisible()
//			muscleTypeBackgroundView.makeVisible()
			buttonAddNewExercise.makeVisible()
			buttonAddMoreExercises.makeInvisible()
			workoutMethodsBtn.makeInvisible()
		} else {
			textViewExercisesNotFound.makeGone()
			textViewClickTheButtonToAdd.makeGone()
//			muscleTypeBackgroundView.makeInvisible()
			buttonAddMoreExercises.makeVisible()
			workoutMethodsBtn.makeVisible()
			buttonAddNewExercise.makeInvisible()
		}

		(exercisesList.adapter as ExercisesListAdapter).update(program)
	}

	private fun updateTopBar() {
		if (!this::program.isInitialized || !this::muscleGroup.isInitialized) { return }

		val muscleText = muscleGroup.title()
		val toolbarText = "$muscleText ${getString(R.string.exercise)}"
		topBarTitleView.text = toolbarText

		val exercises = program.exercises(muscleGroup)
		if (exercises.isNullOrEmpty()) {
			topBarBackBtn.makeVisible()
			topBarDoneBtn.makeGone()
		} else {
			topBarBackBtn.makeGone()
			topBarDoneBtn.makeVisible()
		}
	}

	private fun onDelete(exercise: ResistanceExercise) {
		program.deleteExercise(muscleGroup, exercise)
		Helper.showToast(requireActivity(), R.string.exercise_deleted)
		update()
	}

	private fun onUnlinkSuperSet(exercise: ResistanceExercise) {
		val dialog = YesNoDialog(getString(R.string.are_you_sure_you_want_to_unlink_the_exercises)) { yes ->
			if (yes) {
				program.unlinkSuperset(muscleGroup, exercise)
				update()
			}
		}
		dialog.show(requireActivity().supportFragmentManager, "SelectedExercisesFragment.UnlinkSupersetDialog")
	}

	private fun onCancel() {
		findNavController().navigateUp()
	}

	private fun onDone() {
		val bundle = bundleOf("program" to program)
		navigateUp(REQUEST_CODE, bundle)
	}

	private fun onAddExercises() {
		val action = ExercisesListFragmentDirections.actionToSelectExercise(program, muscleGroup)
		navigate(action, SelectExerciseFragment.REQUEST_CODE)
	}

	private fun onWorkoutMethod() {
		val dialog = ChooseWorkoutMethodDialog()
		dialog.onChoose = { workoutMethod ->
			val defaultSetCount = program.exercises(muscleGroup).filter { it.workoutMethod() == WorkoutMethod.DEFAULT }.count()
			if (defaultSetCount <= 0) {
				OkDialog(Helper.string(R.string.current_exercise_already_defined_with_methods)).show(childFragmentManager, "ExercisesListFragment.NoAvailableExercises.OkDialog")
			} else {
				val action = ExercisesListFragmentDirections.toCreateWorkoutMethodFragment(program.copy(), muscleGroup, workoutMethod)
				navigate(action, CreateWorkoutMethodFragment.REQUEST_CODE)
			}
		}
		dialog.show(childFragmentManager, "ChooseWorkoutMethodDialog")
	}

	private fun onExerciseDetail(exercise: Exercise) {
		val action = ExercisesListFragmentDirections.toExerciseDetail(exercise)
		findNavController().navigate(action)
	}
}