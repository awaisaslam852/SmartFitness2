package com.axles.smartfitness.engine.network.response.program

import com.axles.smartfitness.engine.program.Program

class ApiPrograms(
	val componentList: List<ApiProgram>,
	val total: Int
) {
	fun toPrograms(): List<Program> {
		val results = mutableListOf<Program>()
		componentList.forEach {
			val program = it.toProgram() ?: return@forEach
			results.add(program)
		}
		return results
	}
}