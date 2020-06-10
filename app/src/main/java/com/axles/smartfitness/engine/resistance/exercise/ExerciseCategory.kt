package com.axles.smartfitness.engine.resistance.exercise

import androidx.annotation.DrawableRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import java.util.*

enum class ExerciseCategory(val index: Int) {
	MACHINES(0),
	DUMBBELLS(1),
	BARBELL(2),
	PULLEY_CABLE(3),
	BODY_WEIGHT(4),
	ACCESSORIES(5),
	FAVORITE(6);

	companion object {
		fun fromIndex(index: Int): ExerciseCategory {
			return values().first { it.index == index }
		}

		fun fromTitle(title: String): ExerciseCategory {
			return values().firstOrNull { it.title().toLowerCase(Locale.ENGLISH) === title.toLowerCase(
				Locale.ENGLISH) } ?: MACHINES
		}

		fun fromApi(apiKey: String?): ExerciseCategory? {
			if (apiKey.isNullOrEmpty()) { return null }
			return values().firstOrNull { it.apiKey() == apiKey }
		}
	}

	fun title(): String {
		return when(this) {
			MACHINES -> Helper.string(R.string.machines)
			DUMBBELLS -> Helper.string(R.string.dumbbels)
			BARBELL -> Helper.string(R.string.barbell)
			PULLEY_CABLE -> Helper.string(R.string.pulley_cable)
			BODY_WEIGHT -> Helper.string(R.string.body_weight)
			ACCESSORIES -> Helper.string(R.string.accessories)
			FAVORITE -> Helper.string(R.string.favorite)
			else -> Helper.string(R.string.machines)
		}
	}

	@DrawableRes
	fun iconResource(): Int {
		return when(this) {
			MACHINES -> R.drawable.icon_machines
			DUMBBELLS -> R.drawable.icon_dumbbels
			BARBELL -> R.drawable.icon_barbell
			PULLEY_CABLE -> R.drawable.icon_pulley
			BODY_WEIGHT -> R.drawable.icon_own_weight
			ACCESSORIES -> R.drawable.icon_accessories
			FAVORITE -> R.drawable.icon_favorite_category
			else -> R.drawable.icon_machines
		}
	}

	fun apiKey(): String {
		return when(this) {
			MACHINES -> "Machines"
			DUMBBELLS -> "Dumbbells"
			BARBELL -> "Barbells"
			PULLEY_CABLE -> "Pulley cable"
			BODY_WEIGHT -> "Bodyweight"
			ACCESSORIES -> "Accessories"
			else -> "none"
		}
	}
}