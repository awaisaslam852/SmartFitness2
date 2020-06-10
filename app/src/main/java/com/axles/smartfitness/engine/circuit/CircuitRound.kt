package com.axles.smartfitness.engine.circuit

import android.os.Parcelable
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import kotlinx.android.parcel.Parcelize

@Parcelize
class CircuitRound: ArrayList<CircuitRoundItem>(), Parcelable {
	companion object {
		fun create(): CircuitRound {
			return CircuitRound()
		}

		fun create(exercise: ResistanceExercise): CircuitRound {
			val result =  CircuitRound.create()
			result.add(CircuitRoundItem.create(exercise))
			return result
		}

		fun create(cardioActivity: CardioActivity): CircuitRound {
			val result =  CircuitRound.create()
			result.add(CircuitRoundItem.create(cardioActivity))
			return result
		}
	}

	var restDuration = 45

	fun itemSize() = size
	fun removeItem(item: Any) {
		remove(item)
	}

	fun addItem(item: Any) {
		if (item is ResistanceExercise) add(CircuitRoundItem.create(item))
		if (item is CardioActivity) add(CircuitRoundItem.create(item))
		if (item is CircuitRoundItem) add(item)
	}

	fun restDuration(): String {
		val minute = restDuration / 60
		val second = restDuration % 60
		return String.format("%02d", minute) + ":" + String.format("%02d", second)
	}

	fun exchange(from: Int, to: Int) {
		if (from < size && to < size) {
			val a = get(from)
			val b = get(to)

			removeAt(from)
			add(from, b)

			removeAt(to)
			add(to, a)
		}
	}

	fun copy(): CircuitRound {
		return CircuitRound().also { temp ->
			this.forEach { temp.add(it.copy()) }
			temp.restDuration = this.restDuration
		}
	}
}