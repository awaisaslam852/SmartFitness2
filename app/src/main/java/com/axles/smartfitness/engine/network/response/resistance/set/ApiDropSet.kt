package com.axles.smartfitness.engine.network.response.resistance.set

import com.axles.smartfitness.engine.resistance.set.DropSet
import com.axles.smartfitness.extensions.toSecond

class ApiDropSet(
	val dropSetValues: List<Int> = listOf(),
	val dropKgValues: List<List<Double>> = listOf()
): ApiResistanceSet() {
	fun toDropSet(): DropSet {
		return DropSet(
			reps = dropSetValues.map { it.toString() }.toMutableList(),
			kgs = dropKgValues.map { it.toMutableList() }.toMutableList()
		).also {
			it.id = this.id
			it.set = this.set
			it.restTime = this.restTime.toSecond()
			it.indexOfKg = this.indexOfKgValue
		}
	}
}