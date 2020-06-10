package com.axles.smartfitness.engine.core

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.android.parcel.Parcelize

@Parcelize enum class MuscleGroup(val index: Int): Parcelable {
	CHEST(0),
	BACK(1),
	LEGS(2),
	SHOULDERS(3),
	BICEPS(4),
	TRICEPS(5),
	ABDOMINALS(6),
	FOREARMS(7),
	LOWER_BACK(8);

	companion object {
		fun fromApi(apiKey: String?): MuscleGroup? {
			if (apiKey.isNullOrEmpty()) { return null }
			return values().firstOrNull { it.apiKey() == apiKey }
		}
	}

	fun title(): String {
		return when(index) {
			0 -> Helper.string(R.string.chest)
			1 -> Helper.string(R.string.back)
			2 -> Helper.string(R.string.legs)
			3 -> Helper.string(R.string.shoulders)
			4 -> Helper.string(R.string.biceps)
			5 -> Helper.string(R.string.triceps)
			6 -> Helper.string(R.string.abdominals)
			7 -> Helper.string(R.string.forearms)
			8 -> Helper.string(R.string.lower_back)
			else -> "Not found such value"
		}
	}

	fun apiKey(): String {
		return when(index) {
			0 -> "Chest"
			1 -> "Back"
			2 -> "Legs"
			3 -> "Shoulders"
			4 -> "Biceps"
			5 -> "Triceps"
			6 -> "Abdominals"
			7 -> "Forearms"
			8 -> "Lower back"
			else -> "Not found such value"
		}
	}

	@DrawableRes fun iconResource(): Int {
		return when(index) {
			0 -> R.drawable.pic_chest
			1 -> R.drawable.pic_back
			2 -> R.drawable.pic_legs
			3 -> R.drawable.pic_shoulders
			4 -> R.drawable.pic_biceps
			5 -> R.drawable.pic_triceps
			6 -> R.drawable.pic_abs
			7 -> R.drawable.pic_forearms
			8 -> R.drawable.pic_lowerback
			else -> R.drawable.pic_chest
		}
	}

	@DrawableRes fun largeImageResource(): Int {
		return when(index) {
			0 -> R.drawable.semitransparent_chest
			1 -> R.drawable.semitransparent_back
			2 -> R.drawable.semitransparent_legs
			3 ->R.drawable.semitransparent_shoulders
			4 -> R.drawable.semitransparent_biceps
			5 -> R.drawable.semitransparent_triceps
			6 -> R.drawable.semitransparent_abs
			7 -> R.drawable.semitransparent_forearms
			8 -> R.drawable.semitransparent_lowerback
			else -> R.drawable.semitransparent_chest
		}
	}
}