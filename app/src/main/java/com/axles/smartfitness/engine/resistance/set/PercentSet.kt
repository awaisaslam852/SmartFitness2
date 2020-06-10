package com.axles.smartfitness.engine.resistance.set

class PercentSet(
	var rm: String,
	var kg: MutableList<Double>,
	var percent: Int
): ResistanceSet() {
	fun update(rm: String, kg: Double, percent: Int) {
		this.rm = rm
		this.kg[indexOfKg] = kg
		this.percent = percent
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
		return PercentSet(
			rm,
			kg.toMutableList(),
			percent
		)
	}
}