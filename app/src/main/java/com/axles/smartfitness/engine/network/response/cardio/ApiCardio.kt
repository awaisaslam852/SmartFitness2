package com.axles.smartfitness.engine.network.response.cardio

import com.axles.smartfitness.engine.cardio.CardioActivity

class ApiCardio(
	val state: String = "",
	val items: List<ApiCardioActivity> = listOf()
) {
	fun toActivities(): List<CardioActivity> {
		val results = mutableListOf<CardioActivity>()
		items.forEach {
			val activity = it.toActivity() ?: return@forEach
			results.add(activity)
		}
		return results
	}
}