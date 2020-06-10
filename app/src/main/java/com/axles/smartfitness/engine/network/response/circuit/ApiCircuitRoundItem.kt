package com.axles.smartfitness.engine.network.response.circuit

import com.axles.smartfitness.engine.circuit.CircuitRoundItem
import com.axles.smartfitness.engine.network.response.cardio.ApiCardioActivity
import com.axles.smartfitness.engine.network.response.resistance.exercise.ApiResistanceExercise

class ApiCircuitRoundItem(
	val cardioImageName: String = "",
	val cardioTitle: String = "",
	val cardioExercise: ApiCardioActivity? = null,
	val resistanceExercise: ApiResistanceExercise? = null
) {
	fun toRoundItem(): CircuitRoundItem? {
		if (resistanceExercise != null) {
			val exercise = resistanceExercise.toResistanceExercise() ?: return null
			return CircuitRoundItem.create(exercise)
		}
		if (cardioExercise != null) {
			val cardioActivity = cardioExercise.toActivity() ?: return null
			return CircuitRoundItem.create(cardioActivity)
		}
		return null
	}
}