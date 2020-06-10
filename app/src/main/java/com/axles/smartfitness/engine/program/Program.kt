package com.axles.smartfitness.engine.program

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.axles.smartfitness.BuildConfig
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Program(val type: ProgramType): Parcelable {
	companion object {
		fun create(type: ProgramType): Program {
			return when (type) {
				ProgramType.RESISTANCE -> ResistanceProgram()
				ProgramType.CIRCUIT -> CircuitProgram()
				else -> ResistanceProgram()
			}
		}
	}

	var id = (Math.random()*10000).toInt()
	private var title = ""
	var imageUrl = "https://s3.eu-west-3.amazonaws.com/smartfitnessimages-dev/exercisesImages/37?t=1579703671678"
	var trainee = ""
	var trainer = ""
	var builtAt = Date()
	var updatedAt = Date()

	init {
		if (BuildConfig.DEBUG) {
			trainee = "John Mayer"
			trainer = "Yanai Cohen"
		}
	}

	open fun isNew(): Boolean { return true }
	open fun resetKgIndex() {}
	open fun reset() {}
	open fun copy(): Program { return this }

	fun hasTitle(): Boolean {
		return this.title.isNotEmpty()
	}
	fun setTitle(title: String) { this.title = title }
	fun title(): String {
		if (!title.isBlank()) { return title }
		return when (this) {
			is ResistanceProgram -> "Resistance Program"
			is CardioProgram -> "Cardio Program"
			is CircuitProgram -> "Circuit Program"
			else -> "Resistance Program"
		}
	}

	fun type(): ProgramType {
		if (this is ResistanceProgram) { return ProgramType.RESISTANCE }
		if (this is CircuitProgram) { return ProgramType.CIRCUIT }
		return ProgramType.RESISTANCE
	}
}