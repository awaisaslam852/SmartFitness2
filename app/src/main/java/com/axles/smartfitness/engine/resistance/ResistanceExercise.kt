package com.axles.smartfitness.engine.resistance

import android.os.Parcelable
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.core.WorkoutMethod.*
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.set.ResistanceSet
import com.axles.smartfitness.engine.resistance.set.SuperSet
import kotlinx.android.parcel.Parcelize

@Parcelize
class ResistanceExercise(val exercise: Exercise): Parcelable {
	companion object {
		fun create(exercise: Exercise): ResistanceExercise {
			return ResistanceExercise(exercise).apply {
				this.setWorkoutMethod(DEFAULT)
			}
		}

		fun create(exercise: ResistanceExercise): ResistanceExercise {
			return exercise.copy()
		}

		fun sameMuscleTypeSuperSet(superSets: List<ResistanceExercise>): ResistanceExercise {
			val resistanceExercise = create(superSets[0].exercise)
			resistanceExercise.setWorkoutMethod(SAME_MUSCLE_SUPERSET, superSets.map { it.exercise })
			return resistanceExercise
		}

		fun differentMuscleTypeSuperSet(exercise: ResistanceExercise, superSets: List<Exercise>): ResistanceExercise {
			val resistanceExercise = create(exercise)
			resistanceExercise.setWorkoutMethod(DIFFERENT_MUSCLE_SUPERSET, listOf(exercise.exercise) + superSets)
			return resistanceExercise
		}

		fun workoutMethod(superSets: List<ResistanceExercise>): WorkoutMethod {
			if (superSets.size < 2) { return DEFAULT }

			var foundDifferent = false
			for (index in 1 until  superSets.size) {
				foundDifferent = (superSets[index-1].muscleType != superSets[index].muscleType)
				if (foundDifferent) { break }
			}
			return if (foundDifferent) DIFFERENT_MUSCLE_SUPERSET else SAME_MUSCLE_SUPERSET
		}

		fun machineNumberRange(): List<String> {
			val result = (0 .. 99).toList().map { it.toString() }.toMutableList()
			result.add(0, "--")
			return result
		}
	}

	var id = (Math.random() * 1000).toInt()
	var workoutMethod = DEFAULT
	var sets: MutableList<ResistanceSet> = mutableListOf()
	var note = ""
	var machineNumber: Int = -1

	val muscleType by lazy { exercise.muscleGroup }
	val title by lazy { exercise.title }
	val category by lazy { exercise.category }
	val subCategory by lazy { exercise.subCategory }
	val mainImageUrl by lazy { exercise.mainImageUrl }
	val exerciseImages by lazy { exercise.exerciseImages }
	val descriptions by lazy { exercise.descriptions }
	val videoUrl by lazy { exercise.videoUrl }
	val isOwnExercise by lazy { exercise.isOwnExercise() }

	fun workoutMethod() = workoutMethod

	fun isSuperSet(): Boolean {
		return  workoutMethod().isSuperSet()
	}

	fun parentExercise(): Exercise? {
		try {
			val childExercise = childExercises()
			return childExercise.first()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return null
	}

	fun childExercises(): List<Exercise> {
		if (!isSuperSet()) { return listOf() }

		try {
			return (sets.first() as SuperSet).exercises ?: listOf()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return listOf()
	}

	fun superSetItemsCount(): Int {
		return childExercises().size
	}

	fun setWorkoutMethod(method: WorkoutMethod, exercises: List<Exercise> = listOf()) {
		workoutMethod = method
		sets.clear()
		when (workoutMethod) {
			SAME_MUSCLE_SUPERSET -> {
				sets.add(ResistanceSet.sameSuperSet(exercises))
				sets.add(ResistanceSet.sameSuperSet(exercises))
				sets.add(ResistanceSet.sameSuperSet(exercises))
			}
			DIFFERENT_MUSCLE_SUPERSET -> {
				sets.add(ResistanceSet.differentSuperSet(exercises))
				sets.add(ResistanceSet.differentSuperSet(exercises))
				sets.add(ResistanceSet.differentSuperSet(exercises))
			}
			DROP_SET -> {
				sets.add(ResistanceSet.dropSet(mutableListOf("10", "10"), mutableListOf(15.0, 10.0)))
				sets.add(ResistanceSet.dropSet(mutableListOf("10", "10"), mutableListOf(15.0, 10.0)))
				sets.add(ResistanceSet.dropSet(mutableListOf("10", "10"), mutableListOf(15.0, 10.0)))
			}
			PERCENT -> {
				sets.add(ResistanceSet.percent(4, 25.0, 100))
				sets.add(ResistanceSet.percent(6, 20.0, 85))
				sets.add(ResistanceSet.percent(8, 15.0, 50))
			}
			PYRAMID -> {
				sets.add(ResistanceSet.pyramid(12, 15.0))
				sets.add(ResistanceSet.pyramid(10, 15.0))
				sets.add(ResistanceSet.pyramid(8,15.0))
			}
			DEFAULT -> {
				sets.add(ResistanceSet.default())
			}
		}
	}

	fun addSet() {
		when (workoutMethod()) {
			SAME_MUSCLE_SUPERSET -> {
				val exercises = (sets.first() as SuperSet).exercises
				sets.add(ResistanceSet.sameSuperSet(exercises))
			}
			DIFFERENT_MUSCLE_SUPERSET -> {
				val exercises = (sets.first() as SuperSet).exercises
				sets.add(ResistanceSet.sameSuperSet(exercises))
			}
			DROP_SET -> {
				sets.add(ResistanceSet.dropSet(mutableListOf("8", "8"), mutableListOf(15.0, 15.0)))
			}
			PERCENT -> {
				sets.add(ResistanceSet.percent(5,20.0,80))
			}
			PYRAMID -> {
				sets.add(ResistanceSet.pyramid(6,15.0))
			}
			else -> {}
		}
	}

	fun reduceSet() {
		if (sets.size <= 1) { return }
		sets = sets.subList(0, sets.size - 1)
	}

	fun addSuperSetItems(exercises: List<Exercise>) {
		if (!isSuperSet()) { return }
		for (set in sets) {
			if (!set.isSuperSet()) { continue }
			set.addSuperSetItems(exercises)
		}
	}

	fun kgCount(): Int {
		if (sets.size > 0) { return sets[0].kgCount() }
		return 0
	}

	fun kgIndex() = sets.first().indexOfKg
	fun setKgIndex(index: Int) {
		sets.forEach { it.indexOfKg = index  }
	}

	fun addKgValue() {
		sets.forEach {
			it.addKg()
			it.indexOfKg++
		}
	}

	fun resetKgIndex() {
		sets.forEach { it.resetKgIndex() }
	}

	fun copy(): ResistanceExercise {
		return ResistanceExercise(this.exercise).also {
			it.id = this.id
			it.workoutMethod = this.workoutMethod
			it.sets = this.sets.map { set -> set.copy() }.toMutableList()
			it.note = this.note
			it.machineNumber = this.machineNumber
		}
	}
}

