package com.axles.smartfitness.ui.program

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.toString
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.program.adapter.*
import kotlinx.android.synthetic.main.program_detail_fragment.*
import kotlin.math.min

class ProgramDetailFragment: BaseFragment(R.layout.program_detail_fragment) {
	private val args: ProgramDetailFragmentArgs? by navArgs()
	private lateinit var program: Program

	override fun resolveArguments() {
		if (args == null) { return }
		program = args!!.program
		program.resetKgIndex()
	}

	override fun init() {
		topBarDoneBtn.setOnClickListener { findNavController().navigateUp() }
		timerBtn.setOnClickListener { onTimer() }
	}

	override fun update() {
		programTitleView.text = program.title()
		traineeView.text = program.trainee
		trainerView.text = program.trainer
		buildingDateView.text = program.builtAt.toString("M/d/yy")
		updateDateView.text = program.updatedAt.toString("M/d/yy")

		when (program) {
			is ResistanceProgram -> {
				resistanceContainer.makeVisible()
				val program = this.program as ResistanceProgram
				if (program.hasSplitDays()) {
					resistanceDayRecyclerView.makeVisible()
					val dayCount = program.dayCount()
					if (dayCount == 1) {
						resistanceDayRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
					} else {
						resistanceDayRecyclerView.layoutManager = GridLayoutManager(requireActivity(), min(dayCount, 3))
					}
					resistanceDayRecyclerView.adapter = ResistanceDayAdapter(program,
						onSelectDay = {
							val action = ProgramDetailFragmentDirections.toResistanceDetailPerDayFragment(program, it)
							navigate(action)
						}
					)
					timerBtn.makeInvisible()
				} else {
					resistanceRecyclerView.makeVisible()
					resistanceRecyclerView.adapter = ResistanceAdapter(childFragmentManager, program,
						onExerciseDetail = { onExerciseDetail(it) }
					)
					timerBtn.makeVisible()
				}
			}
			is CircuitProgram -> {
				circuitRecyclerView.makeVisible()
				circuitRecyclerView.adapter = CircuitAdapter(
					program as CircuitProgram,
					this,
					onExerciseDetail = ::onExerciseDetail
				)
			}
			is CardioProgram -> {
				cardioContainer.makeVisible()
				cardioTypeRecyclerView.adapter = CardioActivityTypeAdapter(program as CardioProgram,
					onChoose = {
						(cardioActivitiesRecyclerView.adapter as CardioAdapter).setActivityType(it)
					}
				)
				cardioActivitiesRecyclerView.adapter = CardioAdapter(program as CardioProgram)
			}
		}
	}

	private fun onExerciseDetail(exercise: Exercise) {
		val action = ProgramDetailFragmentDirections.toExerciseDetail(exercise)
		findNavController().navigate(action)
	}

	private fun onTimer() {
		val action = ProgramDetailFragmentDirections.toTimerFragment(program)
		findNavController().navigate(action)
	}
}