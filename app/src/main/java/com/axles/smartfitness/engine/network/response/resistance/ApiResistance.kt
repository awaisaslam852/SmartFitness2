package com.axles.smartfitness.engine.network.response.resistance

import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.network.response.resistance.exercise.ApiResistanceTraining
import java.util.*

class ApiResistance(
	val id: Int = 0,
	val group: GroupResponse,
	val exercisesData: List<ApiResistanceTraining> = listOf()
) {
	class GroupResponse(
		private val id: Int = 0,
		private val group: String = "",
		private val key: String = ""
	) {
		fun splitDay() = SplitDay.values().firstOrNull { it.title().toLowerCase(Locale.US) == group.toLowerCase(Locale.US) }
		fun muscleGroup() = MuscleGroup.values().firstOrNull { it.apiKey().toLowerCase(Locale.US) == key.toLowerCase(Locale.US) }
	}
}