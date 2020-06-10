package com.axles.smartfitness.ui.program.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.ui.program.dialog.KgEditDialog
import com.axles.smartfitness.ui.resistance.exercise.ExerciseSetsListAdapter
import com.axles.smartfitness.view.KgSelectPopupWindow
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.exercise_details_layout.view.*
import kotlinx.android.synthetic.main.exercise_header_item_view.view.*
import kotlinx.android.synthetic.main.exercise_list_item_view.view.*
import kotlinx.android.synthetic.main.program_view_resistance_list_item_view.view.*

class ResistanceAdapter(
	val fragmentManager: FragmentManager,
	val program: ResistanceProgram,
	val splitDay: SplitDay? = null,
	val onExerciseDetail: (Exercise) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	inner class Model(val muscleGroup: MuscleGroup, val exercises: List<ResistanceExercise>)
	private var models: List<Model>

	init {
		models = program.muscleTypesBySplitDay(splitDay).map { Model(it, program.exercises(it)) }
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_view_resistance_list_item_view, parent, false))
	}

	override fun getItemCount(): Int {
		return models.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) { holder.setModel(models[position]) }
	}

	inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: Model
		private lateinit var muscleGroup: MuscleGroup
		private lateinit var exercises: List<ResistanceExercise>

		fun setModel(model: Model) {
			this.model = model
			this.muscleGroup = model.muscleGroup
			this.exercises = model.exercises
			init()
		}

		private fun init() {
			with (itemView) {

			}
			update()
		}

		fun update() {
			with(itemView) {
				val day = program.splitDayByMuscleType(muscleGroup)
				if (day != null) {
					val index = program.splitDays[day]!!.indexOf(muscleGroup)
					indexView.text = "${day.title()}${index+1}"
				} else {
					indexView.text = "${absoluteAdapterPosition+1}"
				}

				muscleIconView.setImageResource(muscleGroup.iconResource())
				muscleTitleView.text = muscleGroup.title()

				if (exercises.isEmpty()) {
					noExercisesView.makeVisible()
					exercisesRecyclerView.makeGone()
				} else {
					noExercisesView.makeGone()
					exercisesRecyclerView.makeVisible()
					exercisesRecyclerView.adapter = ResistanceExerciseListAdapter(exercises)
				}
			}
		}
	}

	inner class ResistanceExerciseListAdapter(val exercises: List<ResistanceExercise>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return ResistanceExerciseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_list_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return exercises.size
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is ResistanceExerciseViewHolder) { holder.setExercise(exercises[position]) }
		}

		inner class ResistanceExerciseViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private lateinit var exercise: ResistanceExercise

			fun setExercise(exercise: ResistanceExercise) {
				this.exercise = exercise
				init()
			}

			private fun init() {
				/*
				itemView.addSetBtn.setOnClickListener { onAddSet() }
				itemView.reduceSetBtn.setOnClickListener { onReduceSet() }
				itemView.deleteBtn.setOnClickListener { onDelete.invoke(exercise) }
				itemView.unlinkSupersetBtn.setOnClickListener { onUnlinkSuperset.invoke(exercise) }
				itemView.editBtn.setOnClickListener { onEdit.invoke(exercise) }
				 */

				with(itemView) {
					headerKgContainer.setOnClickListener { onKg() }

					exerciseHeaderList.setHasFixedSize(false)
					exerciseHeaderList.adapter = ResistanceExerciseHeaderListAdapter(
						exercise = exercise,
						onClick = {shouldExpand -> onExpand(shouldExpand) }
					)

					setsList.setHasFixedSize(false)
					setsList.adapter = ExerciseSetsListAdapter(
						exercise,
						fragmentManager,
						isEditable = false,
						onEditKg = { setIndex ->
							KgEditDialog(exercise = exercise,
								didSave = {
									update()
								}
							).show(fragmentManager, "ResistanceAdapter.KgEditDialog")
						}
					)
				}

				update()
			}

			fun update() {
				with(itemView) {
					programViewDivider.makeVisibleOrGone(absoluteAdapterPosition > 0)
					exerciseHeaderList.adapter?.notifyDataSetChanged()
					setsList.adapter?.notifyDataSetChanged()

					layoutSelectedExercise.setBackgroundColor(Helper.color(R.color.transparent))
					addReduceContainer.makeGone()
					machineNumberPickerDropdownIcon.makeGone()

					pencilIcon.makeGone()
					controlsContainer.makeGone()

					if (exercise.kgCount() <= 1) {
						headerKgTitleView.text = Helper.string(R.string.Kg)
					} else {
						headerKgTitleView.text = "${Helper.string(R.string.Kg).toUpperCase()} ${exercise.kgIndex()+1}"
					}

					noteView.isEnabled = false
					noteView.setText(exercise.note)
				}

				resolveHeader()
				resolveMachineNumber()
			}

			private fun hideAll() {
				val containers = listOf(itemView.headerSuperSetContainer, itemView.headerRepsContainer, itemView.headerRmContainer, itemView.headerDropSetContainer, itemView.headerKgContainer, itemView.headerPercentContainer,
					itemView.addReduceContainer, itemView.machineNumberContainer,
					itemView.deleteBtn, itemView.unlinkSupersetBtn)
				for (container in containers) {
					container.visibility = View.GONE
				}
			}

			private fun resolveHeader() {
				hideAll()
				with (itemView) {
					when (exercise.workoutMethod()) {
						WorkoutMethod.DEFAULT -> {
							itemView.headerRepsContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
							itemView.machineNumberContainer.visibility = View.VISIBLE
							itemView.deleteBtn.visibility = View.VISIBLE
						}
						WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
							itemView.headerSuperSetContainer.visibility = View.VISIBLE
							itemView.headerRepsContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
//							itemView.addReduceContainer.visibility = View.VISIBLE
							itemView.unlinkSupersetBtn.visibility = View.VISIBLE
						}
						WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
							itemView.headerSuperSetContainer.visibility = View.VISIBLE
							itemView.headerRepsContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
//							itemView.addReduceContainer.visibility = View.VISIBLE
							itemView.deleteBtn.visibility = View.VISIBLE
						}
						WorkoutMethod.DROP_SET -> {
							itemView.headerDropSetContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
//							itemView.addReduceContainer.visibility = View.VISIBLE
							itemView.machineNumberContainer.visibility = View.VISIBLE
							itemView.deleteBtn.visibility = View.VISIBLE
						}
						WorkoutMethod.PERCENT -> {
							itemView.headerRmContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
							itemView.headerPercentContainer.visibility = View.VISIBLE
//							itemView.addReduceContainer.visibility = View.VISIBLE
							itemView.machineNumberContainer.visibility = View.VISIBLE
							itemView.deleteBtn.visibility = View.VISIBLE
						}
						WorkoutMethod.PYRAMID -> {
							itemView.headerRepsContainer.visibility = View.VISIBLE
							itemView.headerKgContainer.visibility = View.VISIBLE
//							itemView.addReduceContainer.visibility = View.VISIBLE
							itemView.machineNumberContainer.visibility = View.VISIBLE
							itemView.deleteBtn.visibility = View.VISIBLE
						}
						else -> {}
					}

					if (!exercise.isOwnExercise && exercise.category == ExerciseCategory.BODY_WEIGHT) {
						headerKgContainer.makeGone()
					}

					itemView.kgExpandBtn.makeVisible()
				}
			}

			private fun resolveMachineNumber() {
				if (!itemView.machineNumberContainer.isVisible()) { return }
				val machineNumber = if (exercise.machineNumber < 0) "--" else exercise.machineNumber.toString()
				itemView.machineNumberPickerValueView.text = machineNumber
			}

			private fun onAddSet() {
				exercise.addSet()
				update()
			}

			private fun onReduceSet() {
				if (exercise.sets.size <= 2) {
					Helper.showErrorToast(itemView.context, R.string.the_minimum_is_1_set)
					return
				}
				exercise.reduceSet()
				update()
			}

			private fun onExpand(shouldExpand: Boolean) {
				with(itemView) {
					if (shouldExpand) {
						detailsContainer.expand()
					} else {
						detailsContainer.collapse()
					}
				}
			}

			private fun onKg() {
				val kgCount = exercise.kgCount()
				val kgLabels = mutableListOf<String>()
				for (index in 0 until kgCount ) {
					kgLabels.add("KG${index+1}")
				}
				KgSelectPopupWindow(itemView.headerKgContainer, exercise,
					onSelect = {index, _ ->
						exercise.setKgIndex(index)
						update()
					},
					didAdd = {
						update()
					}
				).show()
			}
		}
	}

	inner class ResistanceExerciseHeaderListAdapter(
		val exercise: ResistanceExercise,
		val onClick: ((Boolean) -> Unit)? = null
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return when (viewType) {
				0 -> { ResistanceExerciseHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_header_item_view, parent, false)) }
				else -> { ResistanceExerciseHeaderDividerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_super_set_divider, parent, false)) }
			}
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is ResistanceExerciseHeaderViewHolder) {
				val method = exercise.workoutMethod()
				if (method == WorkoutMethod.SAME_MUSCLE_SUPERSET || method == WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET) {
					val index = position / 2
					holder.setExercise(exercise.childExercises()[index])
					return
				}

				holder.setExercise(exercise.exercise)
			}
		}

		override fun getItemCount(): Int {
			if (exercise.isSuperSet()) {
				return exercise.childExercises().size * 2 - 1
			}
			return 1
		}

		override fun getItemViewType(position: Int): Int {
			if (exercise.isSuperSet()) {
				return if (position % 2 == 0) 0 else 1
			}
			return 0
		}

		inner class ResistanceExerciseHeaderDividerViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

		inner class ResistanceExerciseHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
			private lateinit var exercise: Exercise
			fun setExercise(exercise: Exercise) {
				this.exercise = exercise
				init()
			}

			private fun init() {
				with(itemView) {
					container.setOnClickListener {
						if (expandBtn.rotation == 0f) {
							expandBtn.rotation = 90f
							onClick?.invoke(true)
						} else {
							expandBtn.rotation = 0f
							onClick?.invoke(false)
						}
					}
					exercisePhotoView.setOnClickListener {
						if (exercise.isOwnExercise()) { return@setOnClickListener }
						onExerciseDetail.invoke(exercise)
					}
				}
				update()
			}

			private fun update() {
				with(itemView) {
					if (absoluteAdapterPosition == 0) { exerciseHeaderExpandContainer.makeVisible() }
					else { exerciseHeaderExpandContainer.makeInvisible() }
					dragAndDropBtn.makeGone()
				}
				if (exercise.isOwnExercise() || exercise.mainImageUrl.isBlank()) {
					Glide.with(itemView.context).load(R.mipmap.ic_launcher).into(itemView.exercisePhotoView)
				} else {
					Glide.with(itemView.context).load(exercise.mainImageUrl).into(itemView.exercisePhotoView)
				}
				itemView.exerciseTitleView.text = exercise.title
			}
		}
	}
}