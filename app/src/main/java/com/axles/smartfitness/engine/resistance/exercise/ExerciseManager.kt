package com.axles.smartfitness.engine.resistance.exercise

import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.network.enqueue
import com.axles.smartfitness.engine.network.response.resistance.exercise.ApiSubCategoryList
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.provider.RetrofitProvider
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

object ExerciseManager: ApiManager() {
	private interface ExerciseApi {
		@GET("subcategories")
		fun getExercises(@Query("muscleGroup") muscleGroup: String, @Query("category") category: String,
						 @Header("Authorization") token: String = UserManager.currentToken(),
						 @Header("Accept-Language") localization: String = LocalizationSettingsManager.apiHeader()): Call<ApiSubCategoryList>
	}

	private val api by lazy { RetrofitProvider.retrofit.create(ExerciseApi::class.java) }

	private val subCategories: MutableList<ExerciseSubCategory> = mutableListOf()
	private val favorites: MutableMap<MuscleGroup, MutableList<Exercise>> = mutableMapOf()

	fun subCategories(muscleGroup: MuscleGroup, category: ExerciseCategory): List<ExerciseSubCategory> {
		return subCategories.filter { it.category == category }
	}

	fun exercises(muscleGroup: MuscleGroup, category: ExerciseCategory): List<Exercise> {
		val subCategories = subCategories(muscleGroup, category).filter { it.muscleGroup == muscleGroup }
		val results = mutableListOf<Exercise>()
		subCategories.forEach { subCategory ->
			results.addAll(subCategory.exercises)
		}
		return results
	}

	fun exercises(muscleGroup: MuscleGroup, category: ExerciseCategory, subCategory: ExerciseSubCategory): List<Exercise> {
		return when {
			subCategory.muscleGroup == muscleGroup && subCategory.category == category -> subCategory.exercises
			else -> listOf()
		}
	}

	fun isFavorite(exercise: Exercise) = exercise.favourite
	fun favorites(muscleGroup: MuscleGroup): List<Exercise> { return favorites[muscleGroup] ?: listOf() }
	fun toggleFavorite(muscleGroup: MuscleGroup, exercise: Exercise) {
		when {
			isFavorite(exercise) -> removeFavorite(muscleGroup, exercise)
			else -> addFavorite(muscleGroup, exercise)
		}
	}
	private fun addFavorite(muscleGroup: MuscleGroup, exercise: Exercise) {
		exercise.favourite = true
		if (favorites[muscleGroup] == null) { favorites[muscleGroup] = mutableListOf() }
		favorites[muscleGroup]?.add(exercise)
	}
	private fun removeFavorite(muscleGroup: MuscleGroup, exercise: Exercise) {
		exercise.favourite = false
		favorites[muscleGroup]?.remove(exercise)
	}

	fun search(muscleGroup: MuscleGroup, key: String): List<Exercise> {
		val results = mutableListOf<Exercise>()
		subCategories.filter { it.muscleGroup == muscleGroup }
			.forEach { subCategory ->
				results.addAll(subCategory.exercises.filter { it.muscleGroup == muscleGroup && it.title.contains(key) })
			}
		return results
	}

	fun getExercises(muscleGroup: MuscleGroup, category: ExerciseCategory, completionListener: CompletionListener) {
		if (subCategories(muscleGroup, category).isNotEmpty()) {
			runCompletionListener(completionListener, null)
			return
		}

		val request = api.getExercises(muscleGroup.apiKey(), category.apiKey())
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when {
					error == null -> {
						subCategories.addAll(response.body()!!.toSubCategories())
						runCompletionListener(completionListener, null)
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = { throwable ->
				runCompletionListener(completionListener, throwable?.localizedMessage)
			}
		}
	}
}