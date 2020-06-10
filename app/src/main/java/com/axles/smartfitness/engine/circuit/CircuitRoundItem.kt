package com.axles.smartfitness.engine.circuit

import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.resistance.ResistanceExercise

class CircuitRoundItem {
	companion object {
		fun create(exercise: ResistanceExercise): CircuitRoundItem {
			return CircuitRoundItem().apply {
				this.resistanceExercise = exercise.copy()
			}
		}

		fun create(cardioActivity: CardioActivity): CircuitRoundItem {
			return CircuitRoundItem().apply { this.cardioActivity = cardioActivity.copy() }
		}
	}

	lateinit var resistanceExercise: ResistanceExercise
	lateinit var cardioActivity: CardioActivity

	fun isResistanceExercise() = this::resistanceExercise.isInitialized
	fun isCardioActivity() = this::cardioActivity.isInitialized
	fun cardioActivityType() = cardioActivity.type

	fun copy(): CircuitRoundItem {
		return if (isResistanceExercise()) {
			create(this.resistanceExercise)
		} else {
			create(this.cardioActivity)
		}
	}
}