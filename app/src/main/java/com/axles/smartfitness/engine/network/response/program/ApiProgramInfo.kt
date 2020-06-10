package com.axles.smartfitness.engine.network.response.program

import java.util.*

data class ApiProgramInfo(
	val buildingDate: Long,
	val updateDate: Long,
	val traineeName: String,
	val trainerName: String,
	val programName: String,
	val imageUrl: String? = null
) {
	fun buildingDate() = Date(buildingDate)
	fun updateDate() = Date(updateDate)
}