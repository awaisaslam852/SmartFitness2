package com.axles.smartfitness.ui.program.adapter

import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter

class CircuitAdapter(
	program: CircuitProgram,
	fragment: BaseFragment,
	onExerciseDetail: (Exercise) -> Unit
): CircuitRoundListAdapter(program, fragment, false, onExerciseDetail)