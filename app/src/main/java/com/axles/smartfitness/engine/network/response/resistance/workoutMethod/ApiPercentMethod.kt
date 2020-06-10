package com.axles.smartfitness.engine.network.response.resistance.workoutMethod

import com.axles.smartfitness.engine.network.response.resistance.set.ApiPercentSet

class ApiPercentMethod(
	val sets: List<ApiPercentSet> = listOf()
): ApiWorkoutMethod()