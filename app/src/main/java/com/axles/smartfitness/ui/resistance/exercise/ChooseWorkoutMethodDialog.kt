package com.axles.smartfitness.ui.resistance.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.core.WorkoutMethod.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.choose_workout_method_dialog.view.*

class ChooseWorkoutMethodDialog: BottomSheetDialogFragment() {
	var onChoose: ((WorkoutMethod) -> Unit)? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		super.onCreateView(inflater, container, savedInstanceState)
		val rootView: View = inflater.inflate(R.layout.choose_workout_method_dialog, container, false)

		rootView.closeBtn.setOnClickListener {
			dismiss()
		}

		rootView.superSetSameMuscleBtn.setOnClickListener {
			onChoose?.invoke(SAME_MUSCLE_SUPERSET)
			dismiss()
		}

		rootView.superSetDifferentMuscleBtn.setOnClickListener {
			onChoose?.invoke(DIFFERENT_MUSCLE_SUPERSET)
			dismiss()
		}

		rootView.dropSetBtn.setOnClickListener {
			onChoose?.invoke(DROP_SET)
			dismiss()
		}

		rootView.pyramidBtn.setOnClickListener {
			onChoose?.invoke(PYRAMID)
			dismiss()
		}

		rootView.percentageBtn.setOnClickListener {
			onChoose?.invoke(PERCENT)
			dismiss()
		}

		return rootView
	}
}