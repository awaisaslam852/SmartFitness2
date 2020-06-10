package com.axles.smartfitness.engine.resistance.set

class PyramidSet(
	var rep: String,
	var kg: MutableList<Double>
): ResistanceSet() {
	fun update(rep: String, kg: Double) {
		this.rep = rep
		this.kg[indexOfKg] = kg
	}

	override fun kgCount(): Int { return kg.size }
	override fun addKg() {
		kg.add(placeHolderKg())
	}
	override fun kgToString(): String {
		if (indexOfKg >= kgCount()) { return "${kg[0]}" }
		val kgValue = kg[indexOfKg]
		return if (isPlaceHolderKg(
				kgValue
			)
		) "?" else "${kg[indexOfKg]}"
	}

	override fun copy(): ResistanceSet {
		return PyramidSet(
			rep,
			kg.toMutableList()
		)
	}
}