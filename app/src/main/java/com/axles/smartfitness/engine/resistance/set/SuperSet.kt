package com.axles.smartfitness.engine.resistance.set

import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.core.WorkoutMethod

class SameSuperSet: SuperSet()
class DifferentSuperSet: SuperSet()

open class SuperSet(
	var exercises: MutableList<Exercise> = mutableListOf(),
	var reps: MutableList<String> = mutableListOf(),
	var kgs: MutableList<MutableList<Double>> = mutableListOf(mutableListOf())
): ResistanceSet() {
	fun addExercises(exercises: List<Exercise>) {
		val currentIndex = this.exercises.size
		for (itemIndex in exercises.indices) {
			val newIndex = currentIndex + itemIndex
			this.exercises.add(newIndex, exercises[itemIndex])
			reps.add(newIndex, 10.toString())
			kgs.add(newIndex, mutableListOf(15.0))
		}
	}

	fun update(reps: List<String>, kgs: MutableList<Double>) {
		this.reps = reps.toMutableList()
		for (index in this.kgs.indices) {
			this.kgs[index][indexOfKg] = kgs[index]
		}
	}

	override fun kgCount(): Int { return kgs[0].size }
	override fun addKg() {
		kgs.forEach { it.add(placeHolderKg()) }
	}
	override fun kgToString(): String {
		if (indexOfKg >= kgCount()) { return kgs.map { it[0] }.joinToString(separator = "-") }
		return kgs.joinToString(separator = "\n") { if (isPlaceHolderKg(it[indexOfKg])) "?" else it[indexOfKg].toString() }
	}

	override fun copy(): ResistanceSet {
		return SuperSet(
			exercises.toMutableList(),
			reps.toMutableList(),
			kgs.map { it.toMutableList() }.toMutableList()
		)
	}
}