package com.axles.smartfitness.engine.resistance.set

class DefaultSet(var rep: String, var kg: MutableList<Double>): ResistanceSet() {
	fun update(set: Int, rep: String, kg: Double) {
		this.set = set
		this.rep = rep
		this.kg[indexOfKg] = kg
	}

	override fun kgCount(): Int { return kg.size }
	override fun addKg() { kg.add(placeHolderKg()) }
	override fun kgToString(): String {
		if (indexOfKg >= kgCount()) { return kg[0].toString() }
		return if (isPlaceHolderKg(kg[indexOfKg])) "?" else kg[indexOfKg].toString()
	}

	override fun copy(): ResistanceSet {
		return DefaultSet(
			rep,
			kg.toMutableList()
		).also {
			it.set = this.set
		}
	}
}