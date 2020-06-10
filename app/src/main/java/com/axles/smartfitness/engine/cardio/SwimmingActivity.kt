package com.axles.smartfitness.engine.cardio

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import kotlinx.android.parcel.Parcelize

@Parcelize class SwimmingActivity: CardioActivity(CardioActivityType.SWIMMING), Parcelable {
	enum class Intensity(@StringRes val swimmingIntensityRes: Int) {
		MAIN_SET(R.string.main_set),
		WARM_UP(R.string.warm_up),
		COOL_DOWN(R.string.cool_down),
		REST(R.string.rest);

		companion object {
			fun fromString(context: Context, intensity: String): Intensity {

				return when (intensity) {
					context.getString(MAIN_SET.swimmingIntensityRes) -> MAIN_SET
					context.getString(WARM_UP.swimmingIntensityRes) -> WARM_UP
					context.getString(COOL_DOWN.swimmingIntensityRes) -> COOL_DOWN
					context.getString(REST.swimmingIntensityRes) -> REST
					else -> MAIN_SET
				}
			}

			fun fromApiKey(key: String) = values().firstOrNull { it.apiKey() == key } ?: MAIN_SET
		}

		fun apiKey(): String {
			return when (this) {
				MAIN_SET -> "Main Set"
				WARM_UP -> "Warm Up"
				COOL_DOWN -> "Cool Down"
				REST -> "Rest"
				else -> ""
			}
		}
	}

	enum class ExerciseType(@StringRes val swimmingExerciseRes: Int) {
		NORMAL(R.string.normal),
		SPRINT(R.string.sprint),
		NO_LEGS(R.string.no_legs),
		NO_HANDS(R.string.no_hands),
		BREATH_HOLD(R.string.breath_hold);

		companion object {
			fun fromString(context : Context, exercise: String):ExerciseType {
				return when (exercise) {
					context.getString(NORMAL.swimmingExerciseRes) -> NORMAL
					context.getString(SPRINT.swimmingExerciseRes) -> SPRINT
					context.getString(NO_LEGS.swimmingExerciseRes) -> NO_LEGS
					context.getString(NO_HANDS.swimmingExerciseRes) -> NO_HANDS
					context.getString(BREATH_HOLD.swimmingExerciseRes) -> BREATH_HOLD
					else -> NORMAL
				}
			}

			fun fromApiKey(key: String) = values().firstOrNull { it.apiKey() == key } ?: NORMAL
		}

		fun apiKey(): String {
			return when (this) {
				NORMAL -> "Normal"
				SPRINT -> "Sprint"
				NO_LEGS -> "No Legs"
				NO_HANDS -> "No Hands"
				BREATH_HOLD -> "Breath Hold"
				else -> ""
			}
		}
	}

	enum class Style(@StringRes val styleRes: Int) {
		FRONT_CRAWL(R.string.front_crawl),
		BREASTSTROKE(R.string.breaststroke),
		BACKSTROKE(R.string.backstroke),
		BUTTERFLY_STROKE(R.string.butterfly_stroke);

		companion object {
			fun fromString(context: Context, styleString: String): Style {
				return when (styleString) {
					context.getString(FRONT_CRAWL.styleRes) -> FRONT_CRAWL
					context.getString(BREASTSTROKE.styleRes) -> BREASTSTROKE
					context.getString(BACKSTROKE.styleRes) -> BACKSTROKE
					context.getString(BUTTERFLY_STROKE.styleRes) -> BUTTERFLY_STROKE
					else -> FRONT_CRAWL
				}
			}

			fun fromApiKey(key: String) = values().firstOrNull { it.apiKey() == key } ?: FRONT_CRAWL
		}

		fun apiKey(): String {
			return when (this) {
				FRONT_CRAWL -> "Front crawl"
				BREASTSTROKE -> "Breaststroke"
				BACKSTROKE -> "Backstroke"
				BUTTERFLY_STROKE -> "Butterfly stroke"
				else -> "Front crawl"
			}
		}
	}

	var distance : Double = 0.0
	var exercise = ExerciseType.NORMAL
	var intensity  = Intensity.MAIN_SET
	var style : Style = Style.FRONT_CRAWL

	override fun isEdited(): Boolean {

		if (timeSeconds != 0) {
			return true
		}

		if (intensity != Intensity.MAIN_SET){
			return true
		}

		if (distance != 0.0){
			return true
		}

		if (exercise != ExerciseType.NORMAL) {
			return true
		}

		if (style != Style.FRONT_CRAWL) {
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return SwimmingActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.exercise = this.exercise
			it.intensity = this.intensity
			it.style = this.style
		}
	}

}