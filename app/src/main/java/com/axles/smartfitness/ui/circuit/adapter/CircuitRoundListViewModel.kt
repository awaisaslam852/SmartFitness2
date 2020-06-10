package com.axles.smartfitness.ui.circuit.adapter

import com.axles.smartfitness.engine.circuit.CircuitRound
import com.axles.smartfitness.engine.circuit.CircuitRoundItem

class CircuitRoundPlaceHolderModel()
class CircuitRoundViewModel(var roundIndex: Int, val round: CircuitRound)
class CircuitRoundItemViewModel(val roundModel: CircuitRoundViewModel, val itemIndex: Int, val item: CircuitRoundItem) {
	val roundIndex by lazy { roundModel.roundIndex }
}