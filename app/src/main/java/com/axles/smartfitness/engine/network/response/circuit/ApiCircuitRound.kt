package com.axles.smartfitness.engine.network.response.circuit

import com.axles.smartfitness.engine.circuit.CircuitRound

class ApiCircuitRound(
	val id: Int = 0,
	val restTime: String = "00:00",
	val items: List<ApiCircuitRoundItem> = listOf()
) {
	fun toRound(): CircuitRound {
		return CircuitRound.create().also {
			items.forEach { response ->
				val roundItem = response.toRoundItem() ?: return@forEach
				it.addItem(roundItem)
			}
		}
	}
}