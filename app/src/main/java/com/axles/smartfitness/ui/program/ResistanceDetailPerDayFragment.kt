package com.axles.smartfitness.ui.program

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.program.adapter.ResistanceAdapter
import kotlinx.android.synthetic.main.fragment_resistance_detail_per_day.*

class ResistanceDetailPerDayFragment: BaseFragment(R.layout.fragment_resistance_detail_per_day) {
	private val args: ResistanceDetailPerDayFragmentArgs? by navArgs()
	private lateinit var program: ResistanceProgram
	private var splitDay: SplitDay? = null

	override fun resolveArguments() {
		if (args == null) { return }
		program = args!!.program as ResistanceProgram
		splitDay = args!!.splitDay
	}

	override fun init() {
		topBarDoneBtn.setOnClickListener { findNavController().navigateUp() }
		timerBtn.setOnClickListener { onTimer() }
		resistanceRecyclerView.adapter = ResistanceAdapter(
			fragmentManager = childFragmentManager,
			splitDay = splitDay,
			program = program,
			onExerciseDetail = { onExerciseDetail(it) }
		)
	}

	override fun update() {

	}

	private fun onExerciseDetail(exercise: Exercise) {
		val action = ResistanceDetailPerDayFragmentDirections.toExerciseDetail(exercise)
		findNavController().navigate(action)
	}

	private fun onTimer() {
		val action = ResistanceDetailPerDayFragmentDirections.toTimerFragment(program)
		findNavController().navigate(action)
	}
}