package com.axles.smartfitness.ui.resistance.exercise

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.resistance.viewModel.ResistanceExerciseListItemViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.exercise_header_item_view.view.*

class ExerciseHeaderListAdapter(
	val viewModel: ResistanceExerciseListItemViewModel,
	val onClick: () -> Unit,
	val onExerciseDetail: (Exercise) -> Unit,
	val onRequestDrag: () -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var resistanceExercise: ResistanceExercise = viewModel.resistanceExercise

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			0 -> { SelectedExerciseHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_header_item_view, parent, false)) }
			else -> { SelectedExerciseHeaderDividerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_super_set_divider, parent, false)) }
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is SelectedExerciseHeaderViewHolder) {
			val method = resistanceExercise.workoutMethod()
			if (method == WorkoutMethod.SAME_MUSCLE_SUPERSET || method == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
				val index = position / 2
				holder.setExercise(resistanceExercise.childExercises()[index])
				return
			}

			holder.setExercise(resistanceExercise.exercise)
		}
	}

	override fun getItemCount(): Int {
		if (resistanceExercise.isSuperSet()) {
			return resistanceExercise.childExercises().size * 2 - 1
		}
		return 1
	}

	override fun getItemViewType(position: Int): Int {
		if (resistanceExercise.isSuperSet()) {
			return if (position % 2 == 0) 0 else 1
		}
		return 0
	}

	inner class SelectedExerciseHeaderDividerViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	inner class SelectedExerciseHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		private lateinit var exercise: Exercise
		fun setExercise(exercise: Exercise) {
			this.exercise = exercise
			init()
		}

		private fun init() {
			with(itemView) {
				container.setOnClickListener { onClick.invoke() }
				exercisePhotoView.setOnClickListener {
					if (exercise.isOwnExercise()) { return@setOnClickListener }
					onExerciseDetail.invoke(exercise)
				}
				dragAndDropBtn.setOnTouchListener { v, event ->
					when (event.action) {
						MotionEvent.ACTION_DOWN -> { onRequestDrag.invoke() }
						MotionEvent.ACTION_UP -> { v.performClick() }
						else -> {}
					}
					true
				}
			}
			update()
		}

		private fun update() {
			with(itemView) {
				if (absoluteAdapterPosition == 0) { exerciseHeaderExpandContainer.makeVisible() }
				else { exerciseHeaderExpandContainer.makeInvisible() }

				updateImage()

				exerciseTitleView.text = exercise.title

				if (viewModel.isExpanded) { expandBtn.rotation = 90f }
				else { expandBtn.rotation = 0f }
			}
		}

		private fun updateImage() {
			with (itemView) {
				if (exercise.isOwnExercise() || exercise.mainImageUrl.isBlank()) {
					Glide.with(context).load(R.mipmap.ic_launcher).into(exercisePhotoView)
				} else {
					Glide.with(context).load(exercise.mainImageUrl).into(exercisePhotoView)
				}
			}
		}
	}
}