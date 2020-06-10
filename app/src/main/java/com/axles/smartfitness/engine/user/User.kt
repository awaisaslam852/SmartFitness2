package com.axles.smartfitness.engine.user

import android.icu.text.CaseMap
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.google.gson.annotations.SerializedName
import java.util.*

class User {
	enum class FitnessLevel {
		Beginner, Intermediate, Expert;

		companion object {
			fun fromText(text: String): FitnessLevel {
				return when (text.toLowerCase(Locale.getDefault())) {
					"intermediate" -> Intermediate
					"expert" -> Expert
					else -> Beginner
				}
			}
		}
	}

	enum class Gender {
		Male, Female;

		companion object {
			fun fromTitle(title: String): Gender {
				return if (title.toLowerCase(Locale.getDefault()) == "male") Male else Female
			}
		}
	}

	@SerializedName("id") val id: Int = 0
	@SerializedName("username") val username = ""
	@SerializedName("email") val email = ""
	@SerializedName("firstName") val firstName = ""
	@SerializedName("lastName") val lastName = ""
	@SerializedName("imageUrl") val imageUrl = ""
	@SerializedName("birthday") var birthDay = Date()
	@SerializedName("gender") var gender: Gender = Gender.Male
	@SerializedName("height") var height: Double = 0.0
	@SerializedName("level") var level: FitnessLevel = FitnessLevel.Beginner
	@SerializedName("usersInBlackLists") var usersInBlackLists = listOf<User>()
	@SerializedName("weight") var weight = 0.0
}