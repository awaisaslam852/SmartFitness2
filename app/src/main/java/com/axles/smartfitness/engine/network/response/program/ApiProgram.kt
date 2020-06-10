package com.axles.smartfitness.engine.network.response.program

import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.network.response.cardio.ApiCardio
import com.axles.smartfitness.engine.network.response.circuit.ApiCircuitRound
import com.axles.smartfitness.engine.network.response.resistance.ApiResistance
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceProgram

class ApiProgram(
	val id: Int = 0,
	val programType: String = "",
	val programInfo: ApiProgramInfo,
	val resistanceProgram: List<ApiResistance> = listOf(),
	val cardioProgram: List<ApiCardio> = listOf(),
	val circuitProgram: List<ApiCircuitRound> = listOf()
) {
	fun toProgram(): Program? {
		val type = programType() ?: return null
		val program = when (type) {
			ProgramType.RESISTANCE -> toResistanceProgram()
			ProgramType.CIRCUIT -> toCircuitProgram()
			ProgramType.CARDIO -> toCardioProgram()
		} ?: return null

		program.id = id
		program.setTitle(programInfo.programName)
		program.trainee = programInfo.traineeName
		program.trainer = programInfo.trainerName
		program.builtAt = programInfo.buildingDate()
		program.updatedAt = programInfo.updateDate()
		program.imageUrl = programInfo.imageUrl ?: ""

		return program
	}

	private fun programType(): ProgramType? {
		return ProgramType.values().firstOrNull { it.apiKey() == programType }
	}

	private fun toResistanceProgram(): ResistanceProgram {
		return ResistanceProgram().also {
			this.resistanceProgram.forEach { response ->
				val muscleGroup = response.group.muscleGroup() ?: return@forEach
				val splitDay = response.group.splitDay()
				if (splitDay != null) {
					it.split(splitDay, muscleGroup)
				}

				response.exercisesData.forEach { exerciseResponse ->
					val resistanceExercise = exerciseResponse.toResistanceExercise() ?: return@forEach
					it.addResistanceExercise(muscleGroup, resistanceExercise)
				}
			}
		}
	}

	private fun toCircuitProgram(): CircuitProgram? {
		return CircuitProgram().also {
			this.circuitProgram.forEach { roundResponse ->
				val round = roundResponse.toRound()
				it.addRound(round)
			}
		}
	}

	private fun toCardioProgram(): CardioProgram? {
		return CardioProgram().also {
			this.cardioProgram.forEach { response ->
				it.addActivities(response.toActivities())
			}
		}
	}
}