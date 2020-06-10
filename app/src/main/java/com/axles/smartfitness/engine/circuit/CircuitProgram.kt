package com.axles.smartfitness.engine.circuit

import android.os.Parcelable
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import kotlinx.android.parcel.Parcelize
import kotlin.math.round

@Parcelize class CircuitProgram: Program(ProgramType.CIRCUIT), Parcelable {
	private var rounds = mutableListOf<CircuitRound>()

	override fun isNew() =  rounds.size == 0
	override fun reset() = rounds.clear()

	override fun resetKgIndex() {
		rounds.forEach { round ->
			round.forEach { item ->
				if (item.isResistanceExercise()) {
					item.resistanceExercise.resetKgIndex()
				}
			}
		}
	}

	fun rounds(): List<CircuitRound> { return rounds }
	fun addNewRound() {
		if (rounds.size > 0 && rounds.last().itemSize() == 0) { return }
		addRound(CircuitRound())
	}

	fun addRound(round: CircuitRound) {
		rounds.add(round)
	}

	fun deleteRound(round: CircuitRound) {
		rounds.remove(round)
	}

	private fun setInitialItem(item: Any) {
		if (rounds.isNotEmpty()) { return }
		if (item is ResistanceExercise) {
			val round = CircuitRound.create(item)
			rounds = mutableListOf(round)
		}

		if (item is CardioActivity) {
			val round = CircuitRound.create(item)
			rounds = mutableListOf(round)
		}
	}

	fun deleteItem(round: CircuitRound, item: CircuitRoundItem) {
		round.removeItem(item)
	}

	fun addItem(item: Any) {
		if (isNew()) {
			setInitialItem(item)
		} else {
			rounds.last().addItem(item)
		}
	}

	fun multiply(count: Int) {
		val currentRounds = mutableListOf<CircuitRound>().also { it.addAll(rounds) }
		for (index in 1 until count) {
			currentRounds.forEach {
				rounds.add(it.copy())
			}
		}
	}

	fun swap(roundFrom: Int, itemFrom: Int, roundTo: Int, itemTo: Int) {
		if (roundFrom >= rounds.size || roundTo >= rounds.size) { return }
		if (roundFrom == roundTo) { rounds[roundFrom].exchange(itemFrom, itemTo) }

		val roundA = rounds[roundFrom]
		val roundB = rounds[roundTo]

		val itemA = roundA[itemFrom]

		roundA.removeItem(itemA)
		roundB.add(itemTo, itemA)
	}
}