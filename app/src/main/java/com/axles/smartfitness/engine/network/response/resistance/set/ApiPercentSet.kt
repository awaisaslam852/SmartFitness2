package com.axles.smartfitness.engine.network.response.resistance.set

import com.axles.smartfitness.engine.resistance.set.PercentSet
import com.axles.smartfitness.engine.resistance.set.PyramidSet
import com.axles.smartfitness.extensions.toSecond

class ApiPercentSet(
	var rm: Int = 0,
	val repsTime: String? = null,
	var kg: List<Double> = listOf(),
	var percent: Int = 0
): ApiResistanceSet() {
	fun toPercentSet(): PercentSet {
		return PercentSet(
			rm = if (repsTime.isNullOrEmpty()) rm.toString() else repsTime,
			kg = kg.toMutableList(),
			percent = percent
		).also {
			it.id = this.id
			it.set = this.set
			it.restTime = this.restTime.toSecond()
			it.indexOfKg = this.indexOfKgValue
		}
	}

	fun toPyramidSet(): PyramidSet {
		return PyramidSet(
			rep = if (repsTime.isNullOrEmpty()) rm.toString() else repsTime,
			kg = kg.toMutableList()
		).also {
			it.id = this.id
			it.set = this.set
			it.restTime = this.restTime.toSecond()
			it.indexOfKg = this.indexOfKgValue
		}
	}
}