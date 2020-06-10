package com.axles.smartfitness.ui.home

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramManager
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.ChooseTrainingProgramDialog
import com.axles.smartfitness.ui.programs.HomeProgramAdapter
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.toolbar_programs.*

class HomeFragment : BaseFragment(R.layout.home_fragment) {
	private var programs: List<Program> = mutableListOf()
		set(value) { field = value
			applyPrograms()
		}

	override fun init() {
		recyclerViewPrograms.layoutManager = StaggeredGridLayoutManager(2, 1)
		recyclerViewPrograms.adapter = HomeProgramAdapter(parentFragmentManager,
			onSelect = ::onSelectProgram,
			onEdit = ::onEditProgram,
			onShare = ::onShareProgram,
			onDelete = ::onDeleteProgram
		)

		setupDrawer()
		setupAddNewProgramButton()
	}

	override fun update() {
		Helper.showLoading(childFragmentManager)
		ProgramManager.getPrograms(object : ApiManager.CompletionListener {
			override fun onCompleted(error: String?) {
				Helper.hideLoading()
				if (error != null) {
					Helper.showErrorAlert(childFragmentManager, error)
					return
				}
				programs = ProgramManager.programs
			}
		})
	}

	private fun applyPrograms() {
		(recyclerViewPrograms.adapter as HomeProgramAdapter).update(programs)
		if (programs.isEmpty()) {
			noProgramContainer.makeVisible()
		} else {
			noProgramContainer.makeGone()
		}
	}

	private fun setupAddNewProgramButton() {
		buttonAddNewProgram.setOnClickListener {
			ChooseTrainingProgramDialog(
				onSelect = { programType ->
					when (programType) {
						ProgramType.RESISTANCE -> {
							val action = HomeFragmentDirections.actionToResistanceTrainingFragment()
							findNavController().navigate(action)
						}
						ProgramType.CIRCUIT -> {
							val action = HomeFragmentDirections.toCircuitTraining()
							findNavController().navigate(action)
						}
						ProgramType.CARDIO -> {
							val action = HomeFragmentDirections.toCardioActivity()
							findNavController().navigate(action)
						}
					}
				}
			).show(childFragmentManager, "HomeFragment.ChooseTrainingDialog")
		}
	}

	private fun setupDrawer() {
		imageButtonBurger.setOnClickListener {
			openDrawer()
		}
	}

	private fun onSelectProgram(program: Program) {
		val action = HomeFragmentDirections.toProgramDetail(program)
		findNavController().navigate(action)
	}

	private fun onEditProgram(program: Program) {
		if (program is ResistanceProgram) {
			val action = HomeFragmentDirections.actionToResistanceTrainingFragment(program)
			findNavController().navigate(action)
		}

		if (program is CircuitProgram) {
			val action = HomeFragmentDirections.toCircuitTraining(program)
			findNavController().navigate(action)
		}

		if (program is CardioProgram) {
			val action = HomeFragmentDirections.toCardioActivity(program)
			findNavController().navigate(action)
		}
	}

	private fun onShareProgram(program: Program) {
		val action = HomeFragmentDirections.toProgramShare(program)
		findNavController().navigate(action)
	}

	private fun onDeleteProgram(program: Program) {
		Helper.showLoading(childFragmentManager)
		ProgramManager.deleteProgram(program, object : ApiManager.CompletionListener {
			override fun onCompleted(error: String?) {
				Helper.hideLoading()
				if (error != null) {
					Helper.showErrorAlert(childFragmentManager, error)
					return
				}
				this@HomeFragment.programs = ProgramManager.programs
			}
		})
	}
}