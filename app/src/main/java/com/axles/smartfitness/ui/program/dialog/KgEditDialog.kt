package com.axles.smartfitness.ui.program.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.resistance.edit.ExerciseEditSetsListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.exercise_edit_dialog.view.*

class KgEditDialog(
	val programType: ProgramType = ProgramType.RESISTANCE,
	val exercise: ResistanceExercise,
	val didSave: (ResistanceExercise) -> Unit
): BottomSheetDialogFragment() {
	lateinit var rootView: View
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		super.onCreateView(inflater, container, savedInstanceState)
		rootView = inflater.inflate(R.layout.kg_edit_dialog, container, false)

		init()

		return rootView
	}

	private fun init() {
		rootView.cancelBtn.setOnClickListener { dismiss() }
		rootView.saveBtn.setOnClickListener { onSave() }
		rootView.topBarBackBtn.setOnClickListener { onBack() }
		rootView.nextBtn.setOnClickListener { onNext() }

		rootView.setsViewPager.adapter = ExerciseEditSetsListAdapter(programType, exercise, kgOnly = true)
		rootView.setsViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				update()
			}
		})
		update()
	}

	private fun update() {
		val itemCount = rootView.setsViewPager.adapter!!.getItemCount()
		val currentPageIndex = rootView.setsViewPager.currentItem

		if (itemCount <= 1) {
			rootView.topBarBackBtn.makeVisibleOrGone(false)
			rootView.nextBtn.makeVisibleOrGone(false)
		}
		rootView.topBarBackBtn.setColorFilter(if (currentPageIndex == 0) Helper.color(R.color.gray) else Helper.color(
			R.color.colorPrimary))
		rootView.nextBtn.setColorFilter(if (currentPageIndex == rootView.setsViewPager.adapter!!.getItemCount()-1) Helper.color(
			R.color.gray) else Helper.color(R.color.colorPrimary))

		val workoutMethod = exercise.workoutMethod()
		when (workoutMethod) {
			WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
				rootView.topBarTitleView.text = "Superset - Set Number ${currentPageIndex+1}"
			}
			WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
				rootView.topBarTitleView.text = "Superset - Set Number ${currentPageIndex+1}"
			}
			WorkoutMethod.DROP_SET -> {
				rootView.topBarTitleView.text = "Dropset - Set Number ${currentPageIndex+1}"
			}
			WorkoutMethod.PYRAMID -> {
				rootView.topBarTitleView.text = "Pyramid - Set Number ${currentPageIndex+1}"
			}
			WorkoutMethod.PERCENT -> {
				rootView.topBarTitleView.text = "Percent of RM - Set Number ${currentPageIndex+1}"
			}
			else -> {
				rootView.topBarTitleView.text = getString(R.string.exercise_details)
			}
		}
	}

	private fun onSave() {
		(rootView.setsViewPager.adapter as ExerciseEditSetsListAdapter).save()
		didSave.invoke(exercise)
		dismiss()
	}

	private fun onBack() {
		if (rootView.setsViewPager.currentItem <= 0) { return }
		rootView.setsViewPager.currentItem = rootView.setsViewPager.currentItem - 1
	}

	private fun onNext() {
		if (rootView.setsViewPager.currentItem >= rootView.setsViewPager.adapter!!.getItemCount()-1) { return }
		rootView.setsViewPager.currentItem = rootView.setsViewPager.currentItem + 1
	}
}