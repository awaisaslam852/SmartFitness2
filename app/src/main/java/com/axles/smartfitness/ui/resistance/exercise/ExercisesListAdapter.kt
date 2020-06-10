package com.axles.smartfitness.ui.resistance.exercise

import android.text.Editable
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.isVisible
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.ui.drag_and_drop.ItemMoveCallback
import com.axles.smartfitness.ui.drag_and_drop.ItemTouchHelperContract
import com.axles.smartfitness.ui.drag_and_drop.StartDragListener
import com.axles.smartfitness.ui.resistance.edit.ExerciseEditDialog
import com.axles.smartfitness.ui.resistance.viewModel.ResistanceExerciseListItemViewModel
import com.axles.smartfitness.view.OnePickerPopupWindow
import kotlinx.android.synthetic.main.exercise_details_layout.view.*
import kotlinx.android.synthetic.main.exercise_list_item_view.view.*
import java.util.*

class ExercisesListAdapter(
	var program: ResistanceProgram,
	val muscleGroup: MuscleGroup,
	val fragmentManager: FragmentManager,
	val onDelete: (ResistanceExercise) -> Unit,
	val onUnlinkSuperset: (ResistanceExercise) -> Unit,
	val onExerciseDetail: (Exercise) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperContract, StartDragListener {
	private var models = listOf<ResistanceExerciseListItemViewModel>()
	private var itemTouchHelper: ItemTouchHelper? = null

	init {
		models = program.exercises(muscleGroup).map {
			ResistanceExerciseListItemViewModel(
				it
			)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_list_item_view, parent, false))
	}

	override fun getItemCount(): Int {
		return models.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as ViewHolder).setModel(models[position])
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		super.onAttachedToRecyclerView(recyclerView)

		val itemMoveCallback = ItemMoveCallback(this)
		itemTouchHelper = ItemTouchHelper(itemMoveCallback)
		itemTouchHelper?.attachToRecyclerView(recyclerView)
	}

	override fun onRowMoved(fromPosition: Int, toPosition: Int) {
		if (fromPosition < models.size && toPosition < models.size) {
			program.exchange(muscleGroup, fromPosition, toPosition)
			Collections.swap(models, fromPosition, toPosition)
		}
		notifyItemMoved(fromPosition, toPosition)
	}

	override fun onRowSelected(viewHolder: RecyclerView.ViewHolder?) {
		if (viewHolder is ViewHolder) {
			viewHolder.itemView.exerciseContainer.cardElevation = 4.dpToPx().toFloat()
		}
	}

	override fun onRowClear(viewHolder: RecyclerView.ViewHolder?) {
		if (viewHolder is ViewHolder) {
			viewHolder.itemView.exerciseContainer.cardElevation = 0f
		}
	}

	override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
		if (viewHolder is ViewHolder) { viewHolder.collapse() }
		itemTouchHelper?.startDrag(viewHolder)
	}

	fun update(program: ResistanceProgram) {
		this.program = program
		models = this.program.exercises(muscleGroup).map {
			ResistanceExerciseListItemViewModel(
				it
			)
		}
		notifyDataSetChanged()
	}

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		private lateinit var model: ResistanceExerciseListItemViewModel
		private lateinit var exercise: ResistanceExercise

		fun setModel(model: ResistanceExerciseListItemViewModel) {
			this.model = model
			this.exercise = model.resistanceExercise as ResistanceExercise
			init()
		}

		private fun init() {
			with (itemView) {
				header.setOnClickListener { onEdit() }
				addSetBtn.setOnClickListener { onAddSet() }
				reduceSetBtn.setOnClickListener { onReduceSet() }
				deleteBtn.setOnClickListener { onDelete.invoke(exercise) }
				unlinkSupersetBtn.setOnClickListener { onUnlinkSuperset.invoke(exercise) }
				editBtn.setOnClickListener { onEdit() }

				exerciseHeaderList.setHasFixedSize(false)
				exerciseHeaderList.adapter = ExerciseHeaderListAdapter(
					viewModel = model,
					onClick = {
						model.isExpanded = !model.isExpanded
						update()
					},
					onExerciseDetail = { this@ExercisesListAdapter.onExerciseDetail.invoke(it) },
					onRequestDrag = { requestDrag(this@ViewHolder) }
				)

				setsList.setHasFixedSize(false)
				setsList.adapter = ExerciseSetsListAdapter(exercise, fragmentManager,
					onEdit = {
						onEdit()
					}
				)

				noteView.addTextChangedListener(object : TextWatcher {
					override fun afterTextChanged(s: Editable?) {
						if (s != null) {
							for (span in s.getSpans(0, s.length, UnderlineSpan::class.java)) {
								s.removeSpan(span)
							}
						}
					}

					override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

					override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
						exercise.note = text?.toString() ?: ""
					}
				})
			}

			update()
		}

		fun update() {
			resolveWorkoutMethod()
			updateMachineNumber()
			resolveExpanded()
		}

		private fun hideAll() {
			with (itemView) {
				listOf(headerSuperSetContainer, headerRepsContainer, headerRmContainer, headerDropSetContainer, headerKgContainer, headerPercentContainer,
					addReduceContainer, machineNumberContainer,
					deleteBtn, unlinkSupersetBtn).forEach { it.makeGone() }
			}
		}

		private fun resolveWorkoutMethod() {
			hideAll()
			with (itemView) {
				when (exercise.workoutMethod()) {
					WorkoutMethod.SAME_MUSCLE_SUPERSET -> {
						headerSuperSetContainer.visibility = View.VISIBLE
						headerRepsContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						addReduceContainer.visibility = View.VISIBLE
						unlinkSupersetBtn.visibility = View.VISIBLE
					}
					WorkoutMethod.DIFFERENT_MUSCLE_SUPERSET -> {
						headerSuperSetContainer.visibility = View.VISIBLE
						headerRepsContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						addReduceContainer.visibility = View.VISIBLE
						deleteBtn.visibility = View.VISIBLE
					}
					WorkoutMethod.DROP_SET -> {
						headerDropSetContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						addReduceContainer.visibility = View.VISIBLE
						deleteBtn.visibility = View.VISIBLE
					}
					WorkoutMethod.PERCENT -> {
						headerRmContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						headerPercentContainer.visibility = View.VISIBLE
						addReduceContainer.visibility = View.VISIBLE
						deleteBtn.visibility = View.VISIBLE
					}
					WorkoutMethod.PYRAMID -> {
						headerRepsContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						addReduceContainer.visibility = View.VISIBLE
						deleteBtn.visibility = View.VISIBLE
					}
					else -> {
						headerRepsContainer.visibility = View.VISIBLE
						headerKgContainer.visibility = View.VISIBLE
						machineNumberContainer.visibility = View.VISIBLE
						deleteBtn.visibility = View.VISIBLE
					}
				}

				if (!exercise.isOwnExercise && exercise.category == ExerciseCategory.BODY_WEIGHT) {
					headerKgContainer.makeGone()
				}
			}
		}

		private fun resolveExpanded() {
			with (itemView) {
				if (model.isExpanded) { detailsContainer.expand() }
				else { detailsContainer.collapse() }
				(exerciseHeaderList.adapter as ExerciseHeaderListAdapter).notifyDataSetChanged()
			}
		}

		private fun updateMachineNumber() {
			with (itemView) {

				if (exercise.isOwnExercise) { machineNumberContainer.makeGone() }
				else if (exercise.workoutMethod() != WorkoutMethod.DEFAULT) {
					machineNumberContainer.makeGone()
				} else if (exercise.category != ExerciseCategory.MACHINES && exercise.category != ExerciseCategory.PULLEY_CABLE) {
					machineNumberContainer.makeGone()
				}
				if (!machineNumberContainer.isVisible()) { return }
				val machineNumber = if (exercise.machineNumber < 0) "--" else exercise.machineNumber.toString()
				machineNumberPickerValueView.text = machineNumber
				val machinePopup = OnePickerPopupWindow(
					machineNumberPicker,
					ResistanceExercise.machineNumberRange()
				) {
					machineNumberPickerValueView.text = it.toString()
					exercise.machineNumber = if (it == "--") -1 else it.toString().toInt()
				}

				machineNumberPicker.setOnClickListener {
					machinePopup.restorePickerState(machineNumber)
					machinePopup.showPopupMenu()
				}
			}
		}

		fun collapse() {
			model.isExpanded = false
			update()
		}

		private fun onAddSet() {
			exercise.addSet()
			(itemView.setsList.adapter as ExerciseSetsListAdapter).update()
		}

		private fun onReduceSet() {
			if (exercise.sets.size <= 1) {
				Helper.showErrorToast(itemView.context, R.string.the_minimum_is_1_set)
				return
			}
			exercise.reduceSet()
			(itemView.setsList.adapter as ExerciseSetsListAdapter).update()
		}

		private fun onEdit() {
			val dialog = ExerciseEditDialog(
				ProgramType.RESISTANCE,
				exercise,
				didSave = {
					(itemView.setsList.adapter as ExerciseSetsListAdapter).notifyDataSetChanged()
				}
			)
			dialog.show(fragmentManager, "ExercisesListFragment.ExerciseEditDialog")
		}
	}
}