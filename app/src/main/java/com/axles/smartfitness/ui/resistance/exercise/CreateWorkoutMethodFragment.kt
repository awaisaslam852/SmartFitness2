package com.axles.smartfitness.ui.resistance.exercise

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager

import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.ChooseMuscleTypeDialog
import com.axles.smartfitness.ui.dialogs.OkDialog
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.resistance.select_exercise.SelectExerciseFragment
import com.phelat.navigationresult.navigateUp
import kotlinx.android.synthetic.main.exercise_create_workout_method_fragment.*
import kotlinx.android.synthetic.main.exercise_create_workout_method_fragment.exercisesList
import kotlinx.android.synthetic.main.exercise_create_workout_method_fragment.muscleTypeBackgroundView
import kotlinx.android.synthetic.main.toolbar_title_cancel.*

class CreateWorkoutMethodFragment : BaseFragment(R.layout.exercise_create_workout_method_fragment) {
	private val args: CreateWorkoutMethodFragmentArgs by navArgs()
	private lateinit var program: ResistanceProgram
	private lateinit var muscleGroup: MuscleGroup
	private lateinit var workoutMethod: WorkoutMethod
	private lateinit var selectedExercise: ResistanceExercise

	companion object {
		const val REQUEST_CODE = 0x0520
	}

	override fun onFragmentResult(requestCode: Int, bundle: Bundle) {
		super.onFragmentResult(requestCode, bundle)
		if (requestCode == SelectExerciseFragment.REQUEST_CODE) {
			if (bundle.containsKey("exercises") && ::selectedExercise.isInitialized && workoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
				val exercises = bundle["exercises"] as List<Exercise>
				val totalSize = selectedExercise.superSetItemsCount() + exercises.size
				if (totalSize > 6) {
					Helper.showErrorToast(requireActivity(), R.string.the_limit_is_6_exercises)
					return
				}
				program.configureDifferentMuscleSuperSet(muscleGroup, selectedExercise, exercises)
				(exercisesList.adapter as ExercisesCreateWorkoutListAdapter).update()
			}
		}
	}

	override fun resolveArguments() {
		program = args.program as ResistanceProgram
		muscleGroup = args.muscleGroup
		workoutMethod = args.workoutMethod
	}

	override fun init() {
		exercisesList.layoutManager = LinearLayoutManager(requireContext())
		exercisesList.adapter = ExercisesCreateWorkoutListAdapter(
			program,
			muscleGroup,
			workoutMethod,
			onDifferentSuperset = { onCreateDifferentSuperset(it) },
			onUnlinkSuperSet = { onUnlinkSuperset(it) }
		)

		cancelBtn.setOnClickListener { onCancel() }
		createWorkoutBtn.setOnClickListener { onCreateWorkout() }
		createSuperSetBtn.setOnClickListener { onCreateSuperSet() }

		finishBtn.setOnClickListener { onFinish() }
	}

	override fun update() {
		applyMethod()
		applyMuscleType()

		(exercisesList.adapter as ExercisesCreateWorkoutListAdapter).notifyDataSetChanged()
	}

	private fun applyMuscleType() {
		muscleTypeBackgroundView.setImageResource(muscleGroup.largeImageResource())
	}

	private fun applyMethod() {
		topBarTitleView.text = "${workoutMethod.title()} Method"
		createWorkoutBtn.text = "Create ${workoutMethod.title()}"
		when (workoutMethod) {
			WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
				createWorkoutBtn.visibility = View.GONE
				createSuperSetBtn.visibility = View.VISIBLE
				finishBtn.visibility = View.VISIBLE
			}
			WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
				createWorkoutBtn.visibility = View.GONE
				createSuperSetBtn.visibility = View.GONE
				finishBtn.visibility = View.VISIBLE
			}
			else -> {
				createWorkoutBtn.visibility = View.VISIBLE
				createSuperSetBtn.visibility = View.GONE
				finishBtn.visibility = View.GONE

				val methodTitle = workoutMethod.title()
				createWorkoutBtn.text = "Create $methodTitle"
			}
		}

		if (workoutMethod != WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
			finishBtn.setBackgroundResource(R.drawable.gradient_blue_vertical_rounded)
		}
	}

	private fun onCancel() {
		findNavController().navigateUp()
	}

	private fun onFinish() {
		val bundle = bundleOf("program" to program)
		navigateUp(REQUEST_CODE, bundle)
	}

	private fun onCreateWorkout() {
		val selectedExercises = (exercisesList.adapter as ExercisesCreateWorkoutListAdapter).selectedExercises()
		for (exercise in selectedExercises) {
			exercise.setWorkoutMethod(workoutMethod)
			Helper.showToast(requireActivity(), "${workoutMethod.title()} ${Helper.string(R.string.created)}")
		}
		onFinish()
	}

	private fun onCreateSuperSet() {
		val selectedExercises = (exercisesList.adapter as ExercisesCreateWorkoutListAdapter).selectedExercises()
		if (selectedExercises.size < 2) {
			Helper.showErrorToast(requireActivity(), R.string.choose_at_least_2_exercises)
			return
		}

		if (selectedExercises.size > 6) {
			Helper.showErrorToast(requireActivity(), R.string.the_limit_is_6_exercises)
			return
		}

		program.createSameMuscleSuperSet(muscleGroup, selectedExercises)
		Helper.showToast(requireActivity(), "${workoutMethod.title()} ${Helper.string(R.string.created)}")

		(exercisesList.adapter as ExercisesCreateWorkoutListAdapter).update()
	}

	private fun onCreateDifferentSuperset(exercise: ResistanceExercise) {
		if (exercise.workoutMethod() == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET && exercise.superSetItemsCount() >= 6) {
			OkDialog(Helper.string(R.string.the_limit_is_6_exercises)).show(childFragmentManager, "CreateWorkoutMethodFragment.OkDialog.DifferentSuperSetLimit")
			return
		}

		val dialog = ChooseMuscleTypeDialog(
			title = Helper.string(R.string.superset_with),
			onChoose = {
				selectedExercise = exercise
				val action = CreateWorkoutMethodFragmentDirections.actionToSelectExercise(
					program = program,
					muscleGroup = it,
					parentExercise = selectedExercise)
				navigate(action, SelectExerciseFragment.REQUEST_CODE)
			}
		)
		dialog.show(childFragmentManager, "SelectedExercisesFragment.ChooseWorkoutMethodDialog")
	}

	private fun onUnlinkSuperset(exercise: ResistanceExercise) {
		val dialog = YesNoDialog(getString(R.string.are_you_sure_you_want_to_unlink_the_exercises)) { yes ->
			if (yes) {
				program.unlinkSuperset(muscleGroup, exercise)
				(exercisesList.adapter as ExercisesCreateWorkoutListAdapter).update()
			}
		}
		dialog.show(requireActivity().supportFragmentManager, "CreateWorkoutMethodFragment.UnlinkSupersetDialog")
	}
}