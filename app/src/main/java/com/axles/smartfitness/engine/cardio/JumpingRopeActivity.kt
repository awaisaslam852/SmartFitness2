package com.axles.smartfitness.engine.cardio

import android.os.Parcelable
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import kotlinx.android.parcel.Parcelize

@Parcelize
class JumpingRopeActivity: CardioActivity(CardioActivityType.JUMPING_ROPE), Parcelable {
	enum class Intensity(index: Int) {
		NORMAL(0),
		FAST(1),
		SLOW(2);

		companion object {
			fun fromTitle(title: String): Intensity {
				try { return values().first { it.title() == title } }
				catch (e: Exception) { e.printStackTrace() }
				return  NORMAL
			}

			fun fromApiKey(key: String) = values().firstOrNull { it.apiKey() == key } ?: NORMAL
		}

		fun title(): String {
			return when (this) {
				NORMAL -> Helper.string(R.string.normal)
				FAST -> Helper.string(R.string.fast)
				SLOW -> Helper.string(R.string.slow)
			}
		}

		fun apiKey(): String {
			return when (this) {
				NORMAL -> "Normal"
				FAST -> "Fast"
				SLOW -> "Slow"
			}
		}
	}

	var intensity : Intensity = Intensity.NORMAL
	var distance : Double = 0.0

	override fun isEdited(): Boolean {

		if (timeSeconds != 0) {
			return true
		}

		if (intensity != Intensity.NORMAL){
			return true
		}

		if (distance != 0.0){
			return true
		}

		return false
	}

	override fun copy(): CardioActivity {
		return JumpingRopeActivity().also {
			it.timeSeconds = this.timeSeconds
			it.distance = this.distance
			it.valueType = this.valueType

			it.intensity = this.intensity
		}
	}
}