package com.axles.smartfitness.engine.resistance

import android.os.Parcelable
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize class ResistanceProgram: Program(ProgramType.RESISTANCE), Parcelable {
	var splitDays: MutableMap<SplitDay, MutableList<MuscleGroup>> = mutableMapOf()
	var exercises: MutableMap<MuscleGroup, MutableList<ResistanceExercise>> = mutableMapOf<MuscleGroup, MutableList<ResistanceExercise>>()

	override fun isNew(): Boolean { return (splitDays.isEmpty() && exercises.isEmpty()) }
	override fun reset() {
		exercises.clear()
	}

	override fun resetKgIndex() {
		exercises.keys.forEach { muscleType ->
			exercises[muscleType]?.forEach { it.resetKgIndex() }
		}
	}

	override fun copy(): ResistanceProgram {
		return ResistanceProgram().also { result ->
			result.id = this.id
			this.splitDays.keys.forEach { splitDay ->
				result.splitDays[splitDay] = this.splitDays[splitDay]!!.toMutableList()
			}
			this.exercises.keys.forEach { muscleType ->
				result.exercises[muscleType] = this.exercises[muscleType]!!.map { it.copy() }.toMutableList()
			}
		}
	}

	fun isEmptyExercises() = exercises.isEmpty()
	fun dayCount(): Int {
		return if (hasExercises(null)) {
			splitDays.keys.size + 1
		} else {
			splitDays.keys.size
		}
	}
	fun hasSplitDays(): Boolean { return splitDays.isNotEmpty() }
	fun splitDayByMuscleType(muscleGroup: MuscleGroup): SplitDay? {
		for (day in splitDays.keys) {
			if (splitDays[day]!!.contains(muscleGroup)) {
				return day
			}
		}
		return null
	}
	fun muscleTypesBySplitDay(splitDay: SplitDay?): List<MuscleGroup> {
		if (splitDay == null) { return exercises.keys.filter { splitDayByMuscleType(it) == null } }
		return splitDays[splitDay] ?: listOf()
	}
	fun hasExercises(splitDay: SplitDay?): Boolean {
		return exercises.keys.filter { splitDayByMuscleType(it) == splitDay && exercises(it).isNotEmpty() }.isNotEmpty()
	}

	fun split(days: Map<SplitDay, MutableList<MuscleGroup>>) {
		this.splitDays.clear()
		days.keys.forEach { day ->
			days[day]?.forEach { muscleType ->
				split(day, muscleType)
			}
		}
	}

	fun split(day: SplitDay, muscleGroup: MuscleGroup) {
		if (!splitDays.containsKey(day)) {
			splitDays[day] = mutableListOf(muscleGroup)
		} else {
			splitDays[day]?.add(muscleGroup)
		}
	}

	fun deleteSplit() {
		splitDays.clear()
	}

	fun exercises(muscleGroup: MuscleGroup): List<ResistanceExercise> { return exercises[muscleGroup] ?: listOf() }
	fun hasMuscleType(muscleGroup: MuscleGroup): Boolean { return exercises(muscleGroup).isNotEmpty() }
	fun hasExercise(muscleGroup: MuscleGroup, exercise: Exercise): Boolean {
		return !this.exercises[muscleGroup]?.filter { it.id == exercise.id }.isNullOrEmpty()
	}
	fun addExercises(muscleGroup: MuscleGroup, exercises: List<ResistanceExercise>) {
		if (this.exercises.containsKey(muscleGroup)) {
			this.exercises.get(muscleGroup)?.addAll(exercises)
		} else {
			this.exercises.put(muscleGroup, exercises.toMutableList())
		}
	}

	fun addExercise(muscleGroup: MuscleGroup, exercise: Exercise) {
		val resistanceExercise = ResistanceExercise.create(exercise)
		addResistanceExercise(muscleGroup, resistanceExercise)
	}

	fun addResistanceExercise(muscleGroup: MuscleGroup, resistanceExercise: ResistanceExercise) {
		if (this.exercises.containsKey(muscleGroup)) {
			this.exercises[muscleGroup]?.add(resistanceExercise)
		} else {
			this.exercises[muscleGroup] =  mutableListOf(resistanceExercise)
		}
	}

	fun deleteExercise(muscleGroup: MuscleGroup, exercise: ResistanceExercise) {
		if (this.exercises.containsKey(muscleGroup)) {
			this.exercises[muscleGroup]?.remove(exercise)
		}
	}

	fun exchange(muscleGroup: MuscleGroup, from: Int, to: Int) {
		if (this.exercises.containsKey(muscleGroup)) {
			Collections.swap(this.exercises[muscleGroup]!!, from, to)
		}
	}

	fun createSameMuscleSuperSet(muscleGroup: MuscleGroup, superSetItems: List<ResistanceExercise>) {
		if (superSetItems.size < 2) { return }
		if (this.exercises.containsKey(muscleGroup)) {
			val exercises = this.exercises[muscleGroup]
			val index = superSetItems.map { exercises?.indexOf(it) }.minBy { it!! }
			if (exercises != null && index != null && index >= 0) {
				val superSet = ResistanceExercise.sameMuscleTypeSuperSet(superSetItems)
				exercises.removeAll(superSetItems)
				exercises.add(index, superSet)
				this.exercises[muscleGroup] = exercises
			}
		}
	}

	fun configureDifferentMuscleSuperSet(muscleGroup: MuscleGroup, exercise: ResistanceExercise, superSetItems: List<Exercise>) {
		if (exercise.isSuperSet()) {
			addDifferentMuscleSuperSet(muscleGroup, exercise, superSetItems)
		} else {
			createDifferentMuscleSuperSet(muscleGroup, exercise, superSetItems)
		}
	}

	private fun createDifferentMuscleSuperSet(muscleGroup: MuscleGroup, baseExercise: ResistanceExercise, superSetItems: List<Exercise>) {
		if (!this.exercises.containsKey(muscleGroup)) { return }

		val exercises = this.exercises[muscleGroup]
		val index = exercises?.indexOf(baseExercise)
		if (exercises != null && index != null && index >= 0) {
			val superSet = ResistanceExercise.differentMuscleTypeSuperSet(baseExercise, superSetItems)
			exercises.removeAt(index)
			exercises.add(index, superSet)
			this.exercises[muscleGroup] = exercises
		}
	}

	private fun addDifferentMuscleSuperSet(muscleGroup: MuscleGroup, exercise: ResistanceExercise, newItems: List<Exercise>) {
		if (!this.exercises.containsKey(muscleGroup)) { return }

		val exercises = this.exercises[muscleGroup]
		val index = exercises?.indexOf(exercise)
		if (exercises != null && index != null && index >= 0) {
			exercises[index].addSuperSetItems(newItems)
			this.exercises[muscleGroup] = exercises
		}
	}

	fun unlinkSuperset(muscleGroup: MuscleGroup, exercise: ResistanceExercise) {
		if (!exercise.isSuperSet()) { return }

		val exercises = this.exercises[muscleGroup] ?: return
		val index = exercises.indexOf(exercise)
		if (index >= 0) {

			when (exercise.workoutMethod()) {
				WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
					exercises.remove(exercise)
					exercises.addAll(index, exercise.childExercises().map { ResistanceExercise.create(it) })
				}
				WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
					exercises.remove(exercise)
					exercises.add(index,  ResistanceExercise.create(exercise.childExercises()[0]))
				}
				else -> {}
			}
			this.exercises[muscleGroup] = exercises
		}
	}
}