package com.axles.smartfitness.engine.resistance.exercise

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Exercise: Parcelable {
	data class Description(@SerializedName("title") val title: String, @SerializedName("description") val content: String)
	data class ImageUrl(val id: Int, val url: String)

	companion object {
		fun create(id: Int, muscleGroup: MuscleGroup, category: ExerciseCategory, subCategory: ExerciseSubCategory, title: String = ""): Exercise {
			return Exercise().apply {
				this.id = id
				this.muscleGroup = muscleGroup
				this.category = category
				this.subCategory = subCategory
				this.title = title
			}
		}

		fun create(id: Int, muscleGroup: MuscleGroup, category: ExerciseCategory, title: String = ""): Exercise {
			return Exercise().apply {
				this.id = id
				this.muscleGroup = muscleGroup
				this.category = category
				this.title = title
			}
		}

		fun createOwnExercise(muscleGroup: MuscleGroup, title: String): Exercise {
			return Exercise().apply {
				this.id = (Math.random()*1000%50).toInt()
				this.muscleGroup = muscleGroup
				this.title = title
			}
		}

		fun machineNumberRange(): List<String> {
			val result = (0 .. 99).toList().map { it.toString() }.toMutableList()
			result.add(0, "--")
			return result
		}
	}

	var id = 0
	lateinit var muscleGroup: MuscleGroup
	lateinit var category: ExerciseCategory
	lateinit var subCategory: ExerciseSubCategory
	var title = ""

	var mainImageUrl = ""
	var exerciseImages = listOf<ImageUrl>()
	var descriptions = listOf<Description>()
	var videoUrl= ""
	var favourite = false

	fun isOwnExercise() = (!this::category.isInitialized)
}