package com.axles.smartfitness.ui.resistance.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.extensions.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.exercise_create_workout_list_item_view.view.*
import kotlinx.android.synthetic.main.exercise_create_workout_header_item_view.view.*

class ExercisesCreateWorkoutListAdapter(
	val program: ResistanceProgram,
	val muscleGroup: MuscleGroup,
	val desiredWorkoutMethod: WorkoutMethod,
	val onDifferentSuperset: ((ResistanceExercise) -> Unit),
	val onUnlinkSuperSet: ((ResistanceExercise) -> Unit)
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	inner class ViewModel(val exercise: ResistanceExercise, var isSelected: Boolean = false)
	private var models: List<ViewModel>
	private var exclusiveExercises = listOf<ResistanceExercise>()
	private var selectedItems = mutableListOf<ResistanceExercise>()

	init {
		exclusiveExercises = program.exercises(muscleGroup).filter { it.workoutMethod() != WorkoutMethod.DEFAULT }
		models = program.exercises(muscleGroup).filter { !exclusiveExercises.contains(it) }.map { ViewModel(it) }
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ExerciseListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_create_workout_list_item_view, parent, false))
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ExerciseListItemViewHolder) {
			holder.setModel(models[position])
		}
	}

	override fun getItemCount(): Int {
		return models.size
	}

	override fun getItemViewType(position: Int): Int { return 0 }

	fun update() {
		models = program.exercises(muscleGroup).filter { !exclusiveExercises.contains(it) }.map { ViewModel(it) }
		selectedItems.clear()
		notifyDataSetChanged()
	}

	fun selectedExercises(): List<ResistanceExercise> {
		return selectedItems
	}

	inner class ExerciseListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var viewModel: ViewModel
		private lateinit var exercise: ResistanceExercise
		fun setModel(model: ViewModel) {
			this.viewModel = model
			this.exercise = model.exercise
			init()
		}

		private fun init() {
			with (itemView) {
				exerciseHeaderList.adapter = HeaderListAdapter(
					resistanceExercise = viewModel.exercise,
					onClick = ::onHeaderItemPressed
				)

				createDifferentSuperSetBtn.setOnClickListener { onDifferentMuscleSuperset() }
				addDifferentSuperSetBtn.setOnClickListener{ onDifferentMuscleSuperset() }
				unlinkSuperSetBtn.setOnClickListener{ onUnlinkSuperSet() }
				setOnClickListener { onContainerPressed() }

				val containers = listOf(checkboxContainer,	superSetContainer, addDifferentSuperSetBtn, createDifferentSuperSetBtn, superSetIndexIndicator)
				for (container in containers) { container.visibility = View.GONE }
			}

			update()
		}

		private fun update() {
			updateCheckbox()
			updateSuperSetRelated()
		}

		private fun updateCheckbox() {
			with (itemView) {
				val workoutMethod = viewModel.exercise.workoutMethod()
				if (workoutMethod.isSuperSet() || desiredWorkoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
					checkboxContainer.makeVisibleOrGone(false)
					checkedStateView.makeVisibleOrGone(false)
					return
				}

				checkboxContainer.makeVisibleOrGone(true)
				if (desiredWorkoutMethod == WorkoutMethod.SAME_MUSCLE_SUPERSET) {
					val checkBoxIcons = listOf(R.drawable.checkbox_1, R.drawable.checkbox_2, R.drawable.checkbox_3,
						R.drawable.checkbox_4, R.drawable.checkbox_5, R.drawable.checkbox_6)
					val index = selectedItems.indexOf(viewModel.exercise)

					superSetIndexIndicator.makeInvisible(!viewModel.isSelected)
//					checkbox.makeVisibleOrGone(!superSetIndexIndicator.isVisible())
					checkedStateView.makeVisibleOrGone(viewModel.isSelected)
					if (superSetIndexIndicator.isVisible()) {
						if (index >= 0 && index < checkBoxIcons.size) { itemView.superSetIndexIndicator.setImageResource(checkBoxIcons[index]) }
					}
					return
				}

				if (viewModel.isSelected) {
					checkbox.setImageResource(R.drawable.check_box_selected)
					checkedStateView.makeVisible()
				} else {
					checkbox.setImageResource(R.drawable.check_box_not_selected)
					checkedStateView.makeGone()
				}
			}
		}

		private fun updateSuperSetRelated() {
			val workoutMethod = viewModel.exercise.workoutMethod()
			itemView.superSetContainer.makeVisibleOrGone(workoutMethod.isSuperSet())
			itemView.unlinkSuperSetBtn.makeVisibleOrGone(workoutMethod.isSuperSet())
			itemView.addDifferentSuperSetBtn.makeVisibleOrGone(workoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET)
			itemView.createDifferentSuperSetBtn.makeVisibleOrGone(!workoutMethod.isSuperSet() && desiredWorkoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET)
		}

		private fun toggleSelected() {
			viewModel.isSelected = !viewModel.isSelected
			if (viewModel.isSelected) {
				selectedItems.add(viewModel.exercise)
			} else {
				selectedItems.remove(viewModel.exercise)
			}
			notifyDataSetChanged()
		}

		private fun onContainerPressed() {
			if (desiredWorkoutMethod == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
				onDifferentMuscleSuperset()
				return
			}
			toggleSelected()
		}

		private fun onDifferentMuscleSuperset() {
			onDifferentSuperset.invoke(viewModel.exercise)
		}

		private fun onUnlinkSuperSet() {
			onUnlinkSuperSet.invoke(viewModel.exercise)
		}

		private fun onCheckbox(isChecked: Boolean) {
			viewModel.isSelected = isChecked
			update()
		}

		private fun onHeaderItemPressed() {
			onContainerPressed()
		}
	}

	inner class HeaderListAdapter(
		protected val resistanceExercise: ResistanceExercise,
		protected val onClick: (() -> Unit)? = null
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return when (viewType) {
				0 -> { HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_create_workout_header_item_view, parent, false)) }
				else -> { HeaderDividerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_super_set_divider, parent, false)) }
			}
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is HeaderViewHolder) {
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

		inner class HeaderDividerViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

		inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
			private lateinit var exercise: Exercise
			fun setExercise(exercise: Exercise) {
				this.exercise = exercise
				init()
			}

			private fun init() {
				update()
			}

			private fun update() {
				if (exercise.mainImageUrl.isBlank()) {
					Glide.with(itemView.context).load(R.mipmap.ic_launcher).into(itemView.exercisePhotoView)
				} else {
					Glide.with(itemView.context).load(exercise.mainImageUrl).into(itemView.exercisePhotoView)
				}
				itemView.exerciseTitleView.text = exercise.title
				itemView.setOnClickListener { onClick?.invoke() }
			}
		}
	}
}