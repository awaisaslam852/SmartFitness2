package com.axles.smartfitness.engine.resistance.set

class DropSet(
	var reps: MutableList<String>,
	var kgs: MutableList<MutableList<Double>>
): ResistanceSet() {
	override fun addValue() {
		if (reps.count() < 6) {
			reps.add(10.toString())
			kgs[0].add(10.0)
		}
	}

	override fun reduceValue() {
		if (reps.size > 2) {
			reps = reps.subList(0, reps.size-1)
			kgs[0] = kgs[0].subList(0, kgs[0].size-1)
		}
	}

	fun update(reps: List<String>, kgs: MutableList<Double>) {
		this.reps = reps.toMutableList()
		this.kgs[indexOfKg] = kgs
	}

	override fun kgCount(): Int { return kgs.size }
	override fun addKg() {
		kgs.add(kgs[0].map { placeHolderKg() }.toMutableList())
	}

	override fun kgToString(): String {
		if (indexOfKg >= kgCount()) { return kgs[0].joinToString(separator = "-") }
		return kgs[indexOfKg].joinToString(separator = "-") { if (isPlaceHolderKg(it)) "?" else it.toString() }
	}

	override fun copy(): ResistanceSet {
		return DropSet(
			reps.toMutableList(),
			kgs.map { it.toMutableList() }.toMutableList()
		)
	}
}