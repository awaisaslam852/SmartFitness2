package com.axles.smartfitness.engine.network.response.resistance.workoutMethod

import com.axles.smartfitness.engine.network.response.resistance.set.ApiDropSet

class ApiDropSetMethod(
	val sets: List<ApiDropSet> = listOf()
): ApiWorkoutMethod()