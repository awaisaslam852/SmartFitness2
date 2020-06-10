package com.axles.smartfitness.ui.resistance.select_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseManager
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.resistance.viewModel.ExerciseListItemViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.subcategory_exercise_list_item.view.*

open class ExerciseAdapter(
	val muscleGroup: MuscleGroup,
	var exercises: List<ExerciseListItemViewModel>,
	val onUpdate: () -> Unit,
	val onDetail: ((Exercise) -> Unit)? = null,
	val onSelected: ((ExerciseListItemViewModel, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
		return ExerciseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subcategory_exercise_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		return exercises.size
	}

	override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
		holder.setModel(exercises[position])
	}

	inner class ExerciseViewHolder(view: View): RecyclerView.ViewHolder(view){
		private lateinit var viewModel: ExerciseListItemViewModel
		private lateinit var exercise: Exercise
		fun setModel(viewModel: ExerciseListItemViewModel) {
			this.viewModel = viewModel
			this.exercise = viewModel.exercise
			init()
		}

		private fun init() {
			with (itemView) {
				setOnLongClickListener {
					ExerciseManager.toggleFavorite(muscleGroup, exercise)
					if (ExerciseManager.isFavorite(exercise)) {
						Toast.makeText(context, R.string.exercise_added_to_favorites, Toast.LENGTH_LONG).show()
					} else {
						Toast.makeText(context, R.string.exercise_removed_from_favorites, Toast.LENGTH_LONG).show()
					}
					onUpdate.invoke()

					notifyDataSetChanged()
					true
				}

				layoutExercise.setOnClickListener {
					viewModel.isSelected = !viewModel.isSelected
					update()
					onSelected?.invoke(viewModel, viewModel.isSelected)
				}

				exercisePhotoView.setOnClickListener {
					onDetail?.invoke(exercise)
				}
			}

			update()
		}

		private fun update() {
			with(itemView) {
				val exercise = viewModel.exercise
				topBarTitleView.text = exercise.title
				if (exercise.mainImageUrl.isNotBlank()) {
					Glide.with(context).load(exercise.mainImageUrl).into(exercisePhotoView)
				}

				imageViewFavorite.makeVisibleOrGone(ExerciseManager.isFavorite(exercise))

				if (viewModel.isSelected) {
					checkboxSubcategoryExercise.setImageResource(R.drawable.check_box_selected)
				} else {
					checkboxSubcategoryExercise.setImageResource(R.drawable.check_box_not_selected)
				}
			}
		}
	}

}