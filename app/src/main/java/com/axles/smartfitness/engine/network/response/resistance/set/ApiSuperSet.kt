package com.axles.smartfitness.engine.network.response.resistance.set

import com.axles.smartfitness.engine.network.response.resistance.exercise.ApiResistanceExercise
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.set.DifferentSuperSet
import com.axles.smartfitness.engine.resistance.set.SameSuperSet
import com.axles.smartfitness.extensions.toSecond

class ApiSuperSet(
	val setNumber: Int = 0,
	val notes: String = "",
	val exercises: List<ApiResistanceExercise> = listOf()
): ApiResistanceSet() {
	fun toSameSuperSet(exercises: List<Exercise>): SameSuperSet {
		return SameSuperSet().also {
			it.id = this.id
			it.exercises = exercises.toMutableList()
			it.reps = this.exercises.map { response -> response.toDefaultSet().rep }.toMutableList()
			it.kgs = this.exercises.map { response -> response.toDefaultSet().kg.toMutableList() }.toMutableList()
			it.set = this.set
			it.restTime = this.restTime.toSecond()
			it.indexOfKg = this.indexOfKgValue
		}
	}

	fun toDifferentSuperSet(exercises: List<Exercise>): DifferentSuperSet {
		return DifferentSuperSet().also {
			it.id = this.id
			it.exercises = exercises.toMutableList()
			it.reps = this.exercises.map { response -> response.toDefaultSet().rep }.toMutableList()
			it.kgs = this.exercises.map { response -> response.toDefaultSet().kg.toMutableList() }.toMutableList()
			it.set = this.set
			it.restTime = this.restTime.toSecond()
			it.indexOfKg = this.indexOfKgValue
		}
	}
}
