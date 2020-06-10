package com.axles.smartfitness.engine.network.response.resistance.exercise

import com.axles.smartfitness.engine.resistance.exercise.ExerciseSubCategory

class ApiSubCategoryList(
	val componentList: List<ApiCategoryContainer> = listOf(),
	val total: Int = 0
) {
	class ApiCategoryContainer(
		val category: String = "",
		val subcategory: List<ApiSubCategory> = listOf()
	) {
		fun subcategories() = subcategory.filter { it.toSubCategory() != null }.map { it.toSubCategory()!! }
	}

	fun toSubCategories(): List<ExerciseSubCategory> {
		val results = mutableListOf<ExerciseSubCategory>()
		componentList.forEach {
			results.addAll(it.subcategories())
		}
		return results
	}
}