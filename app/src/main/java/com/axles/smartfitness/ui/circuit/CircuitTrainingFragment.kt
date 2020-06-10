package com.axles.smartfitness.ui.circuit

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.program.ProgramManager
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter
import com.axles.smartfitness.ui.dialogs.ChooseCardioActivityTypeDialog
import com.axles.smartfitness.ui.dialogs.ChooseMuscleTypeDialog
import com.axles.smartfitness.ui.dialogs.MultiplicationDialog
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.resistance.ResistanceTrainingFragment
import com.axles.smartfitness.ui.resistance.select_exercise.SelectExerciseFragment
import kotlinx.android.synthetic.main.circuit_training_fragment.*

class CircuitTrainingFragment: BaseFragment(R.layout.circuit_training_fragment) {
	private val args: CircuitTrainingFragmentArgs? by navArgs()

	private var program = Program.create(ProgramType.CIRCUIT) as CircuitProgram

	override fun onFragmentResult(requestCode: Int, bundle: Bundle) {
		super.onFragmentResult(requestCode, bundle)
		if (requestCode == SelectExerciseFragment.REQUEST_CODE) {
			val exercise = bundle.get(Constants.Bundle.Exercise) as Exercise
			val resistanceExercise = ResistanceExercise.create(exercise)
			program.addItem(resistanceExercise)
			update()
		}
	}

	override fun resolveArguments() {
		if (args == null) { return }
		if (args!!.program != null) {
			program = args!!.program as CircuitProgram
			program.resetKgIndex()
			return
		}
	}

	override fun init() {
		topBarBackBtn.setOnClickListener { onBack() }
		topBarDiscardBtn.setOnClickListener { onDiscard() }
		topBarSaveBtn.setOnClickListener { onSave() }

		roundListRecyclerView.setHasFixedSize(true)
		roundListRecyclerView.adapter = CircuitRoundListAdapter(
			program,
			this,
			onExerciseDetail = { onExerciseDetail(it) },
			onAddResistance = { onAddResistance() },
			onAddCardio = { onAddNewCardio() },
			onEmpty = { update() }
		)

		noCircuitAddNewResistanceButton.setOnClickListener { onAddResistance() }
		noCircuitAddNewCardioButton.setOnClickListener { onAddNewCardio() }

		addRoundBtn.setOnClickListener { onAddRound() }
		multiplyBtn.setOnClickListener { onMultiply() }
	}

	override fun update() {
		if (program.isNew()) {
			noCircuitContainer.makeVisible()
			circuitContainer.makeGone()

			topBarSaveBtn.setTextColor(Helper.color(R.color.gray))
			topBarSaveBtn.isEnabled = false
			topBarDiscardBtn.makeGone()
			topBarBackBtn.makeVisible()
		} else {
			noCircuitContainer.makeGone()
			circuitContainer.makeVisible()

			topBarSaveBtn.setTextColor(Helper.color(R.color.white))
			topBarSaveBtn.isEnabled = true
			topBarDiscardBtn.makeVisible()
			topBarBackBtn.makeGone()

			(roundListRecyclerView.adapter as CircuitRoundListAdapter).update()
		}
	}

	private fun onBack() {
		if (program.isNew()) {
			findNavController().navigateUp()
			return
		}

		onDiscard()
	}

	private fun onDiscard() {
		if (ProgramManager.isSavedProgram(program)) {
			val dialog = YesNoDialog(getString(R.string.q_leave_this_page_withtout_saving)) { yes ->
				if (yes) {
					findNavController().navigateUp()
				}
			}
			dialog.show(childFragmentManager, ResistanceTrainingFragment.DISCARD_DIALOG)
			return
		}

		val dialog = YesNoDialog(getString(R.string.are_you_sure_you_want_to_delete_this_program)) { yes ->
			if (yes) {
				findNavController().navigateUp()
			}
		}
		dialog.show(requireActivity().supportFragmentManager, "CircuitTrainingFragment.Discard")
	}

	private fun onSave() {
		val action =
			CircuitTrainingFragmentDirections.toProgramFulfill(
				program
			)
		findNavController().navigate(action)
	}

	private fun onAddResistance() {
		val dialog = ChooseMuscleTypeDialog(
			title = Helper.string(R.string.from_what_muscle),
			onChoose = {
				val action = CircuitTrainingFragmentDirections.actionToSelectExercise(program, it)
				navigate(action, SelectExerciseFragment.REQUEST_CODE)
			}
		)
		dialog.show(childFragmentManager, "CircuitTrainingFragment.ChooseMuscleTypeDialog")
		update()
	}

	private fun onAddNewCardio() {
		val dialog = ChooseCardioActivityTypeDialog(
			onChoose = {
				val cardioActivity = CardioActivity.create(it)
				program.addItem(cardioActivity)
				update()
			}
		)
		dialog.show(childFragmentManager, "CircuitTrainingFragment.ChooseCardioActivityTypeDialog")
	}

	private fun onAddRound() {
		if (program.rounds().last().isEmpty()) {
			Helper.showErrorToast(requireActivity(), R.string.add_at_least_one_exercise_to_round)
			return
		}
		program.addNewRound()
		update()
	}

	private fun onMultiply() {
		MultiplicationDialog(
			onChoose = {
				program.multiply(it)
				update()
			}
		).show(childFragmentManager, "CircuitTrainingFragment.MultiplicationDialog")
	}

	private fun onExerciseDetail(exercise: Exercise) {
		val action =
			CircuitTrainingFragmentDirections.toExerciseDetail(
				exercise
			)
		findNavController().navigate(action)
	}
}