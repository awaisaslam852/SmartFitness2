package com.axles.smartfitness.ui.resistance.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.resistance.*
import com.axles.smartfitness.engine.resistance.exercise.Exercise.*
import com.axles.smartfitness.engine.core.WorkoutMethod.*
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.set.*
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.view.MultiplePickerPopupWindow
import com.axles.smartfitness.view.OnePickerPopupWindow
import com.axles.smartfitness.view.SingleSelectPopupWindow
import com.axles.smartfitness.view.TimePickerPopupWindow
import kotlinx.android.synthetic.main.exercise_edit_set_horizontal_list_item_view.view.*
import kotlinx.android.synthetic.main.exercise_edit_set_vertical_list_item_view.view.*
import kotlinx.android.synthetic.main.exercise_edit_set_vertical_list_item_view.view.repsTitleView
import kotlinx.android.synthetic.main.exercise_edit_set_value_list_item_view.view.*
import kotlinx.android.synthetic.main.picker_layout.view.*
import kotlin.math.max

class ExerciseEditSetsListAdapter(
	val programType: ProgramType = ProgramType.RESISTANCE,
	val exercise: ResistanceExercise,
	val kgOnly: Boolean = false
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private lateinit var workoutMethod: WorkoutMethod
	inner class SetViewModel(val set: ResistanceSet) {
		var default: DefaultSet? = null
		var superSet: SuperSet? = null
		var dropSet: DropSet? = null
		var percent: PercentSet? = null
		var pyramid: PyramidSet? = null

		var setModel: ValueViewModel? = null
		var repModel: ValueViewModel? = null
		var rmModel: ValueViewModel? = null
		var kgModel: ValueViewModel? = null
		var percentModel: ValueViewModel? = null

		var repsModel: ListValueViewModel? = null
		var kgsModel: ListValueViewModel? = null

		init {
			workoutMethod = set.workoutMethod()
			when (workoutMethod) {
				SAME_MUSCLE_SUPERSET -> { superSet = set as SuperSet
				}
				DIFFERENT_MUSCLE_SUPERSET -> { superSet = set as SuperSet
				}
				DROP_SET -> { dropSet = set as DropSet
				}
				PERCENT -> { percent = set as PercentSet
				}
				PYRAMID -> { pyramid = set as PyramidSet
				}
				else -> { default = set as DefaultSet
				}
			}
		}
	}

	inner class ValueViewModel(var value: Any, var index: Int = 0) {
		fun intValue(): Int {
			if (value is String) { return (value as String).toInt()	}
			if (value is Double) { return (value as Double).toInt() }
			return (value as Int)
		}

		fun doubleValue(): Double {
			if (value is String) { return (value as String).toDouble() }
			if (value is Int) { return (value as Int).toDouble() }
			return (value as Double)
		}

		fun stringValue(): String {
			return value.toString()
		}
	}
	inner class ListValueViewModel(var models: List<ValueViewModel>)

	inner class SingleSetListItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: SetViewModel
		fun setModel(setModel: SetViewModel) {
			this.model = setModel
			init()
		}

		private fun init() {
			when (workoutMethod) {
				PERCENT -> {
					val set = model.percent!!
					model.rmModel = ValueViewModel(set.rm)
					model.percentModel = ValueViewModel(set.percent)

					val currentKg = set.kg[set.indexOfKg]
					if (ResistanceSet.isPlaceHolderKg(currentKg)) {
						model.kgModel = ValueViewModel(0.0)
					} else {
						model.kgModel = ValueViewModel(currentKg)
					}
				}
				PYRAMID -> {
					val set = model.pyramid!!
					model.repModel = ValueViewModel(set.rep)

					val currentKg = set.kg[set.indexOfKg]
					if (ResistanceSet.isPlaceHolderKg(currentKg)) {
						model.kgModel = ValueViewModel(0.0)
					} else {
						model.kgModel = ValueViewModel(currentKg)
					}
				}
				else -> {
					val set = model.default!!
					model.setModel = ValueViewModel(set.set)
					model.repModel = ValueViewModel(set.rep)

					val currentKg = set.kg[set.indexOfKg]
					if (ResistanceSet.isPlaceHolderKg(currentKg)) {
						model.kgModel = ValueViewModel(0.0)
					} else {
						model.kgModel = ValueViewModel(currentKg)
					}
				}
			}

			itemView.setContainer.setOnClickListener { onSet() }
			itemView.repContainer.setOnClickListener { onRep() }
			itemView.rmContainer.setOnClickListener { onRM() }
			itemView.timeContainer.setOnClickListener { onTime() }
			itemView.kgContainer.setOnClickListener { onKg() }
			itemView.percentContainer.setOnClickListener { onPercent() }

			itemView.repsToTimeBtn.setOnClickListener { showToTimePopup(it) }
			itemView.rmToTimeBtn.setOnClickListener { showToTimePopup(it) }
			itemView.timeToRepsBtn.setOnClickListener { showFromTimePopup() }

			val containers = listOf(itemView.setContainer, itemView.repContainer, itemView.rmContainer, itemView.timeContainer, itemView.kgContainer, itemView.percentContainer)
			containers.map { it.makeVisibleOrGone(false) }

			if (kgOnly) {
				itemView.kgContainer.makeVisible()
				update()
				return
			}

			when (workoutMethod) {
				PERCENT -> {
					if (model.rmModel != null && (model.rmModel!!.value as String).contains(":")) {
						itemView.timeContainer.makeVisibleOrGone(true)
					} else {
						itemView.rmContainer.makeVisibleOrGone(true)
					}
					itemView.kgContainer.makeVisibleOrGone(true)
					itemView.percentContainer.makeVisibleOrGone(true)
				}
				PYRAMID -> {
					if (model.repModel != null && (model.repModel!!.value as String).contains(":")) {
						itemView.timeContainer.makeVisibleOrGone(true)
					} else {
						itemView.repContainer.makeVisibleOrGone(true)
					}
					itemView.kgContainer.makeVisibleOrGone(true)
				}
				else -> {
					itemView.setContainer.makeVisibleOrGone(true)
					if (model.repModel != null && (model.repModel!!.value as String).contains(":")) {
						itemView.timeContainer.makeVisibleOrGone(true)
					} else {
						itemView.repContainer.makeVisibleOrGone(true)
					}
					itemView.kgContainer.makeVisibleOrGone(true)
				}
			}

			update()
		}

		private fun update() {
			if (programType != ProgramType.RESISTANCE) {
				itemView.setContainer.makeGone()
				if (!exercise.isOwnExercise && (exercise.category == ExerciseCategory.BODY_WEIGHT || exercise.category == ExerciseCategory.ACCESSORIES)) {
					itemView.kgContainer.makeGone()
				}
			}

			if (model.setModel != null) {
				itemView.setPicker.valueView.text = model.setModel!!.value.toString()
			}

			if (model.repModel != null) {
				if (!model.repModel!!.value.isTimeText()) {
					itemView.repPicker.valueView.text = model.repModel!!.value.toString()
				} else {
					itemView.timePicker.valueView.text = model.repModel!!.value.toString()
				}
			}

			if (model.rmModel != null) {
				if (!model.rmModel!!.value.isTimeText()) {
					itemView.repPicker.valueView.text = model.rmModel!!.value.toString()
				} else {
					itemView.timePicker.valueView.text = model.rmModel!!.value.toString()
				}
			}

			if (model.kgModel != null) {
				itemView.kgPicker.valueView.text = model.kgModel!!.value.toString()
			}

			if (model.percentModel != null) {
				itemView.percentPicker.valueView.text = model.percentModel!!.value.toString() + "%"
			}
		}

		private fun showToTimePopup(view: View) {
			val popupWindow = SingleSelectPopupWindow(
				view,
				"Time",
				onSelect = {
					itemView.repContainer.makeVisibleOrGone(false)
					itemView.rmContainer.makeVisibleOrGone(false)
					itemView.timeContainer.makeVisibleOrGone(true)
				}
			)
			popupWindow.show()
		}

		private fun showFromTimePopup() {
			val title = if (model.repModel != null) "Reps" else "RM"
			val popupWindow = SingleSelectPopupWindow(
				itemView.timeToRepsBtn,
				title,
				onSelect = {
					itemView.timeContainer.makeVisibleOrGone(false)
					if (model.repModel != null) { itemView.repContainer.makeVisibleOrGone(true) }
					if (model.rmModel != null) { itemView.rmContainer.makeVisibleOrGone(true) }
				}
			)
			popupWindow.show()
		}

		private fun onSet() {
			if (workoutMethod != DEFAULT) { return }

			val popupWindow = OnePickerPopupWindow(
				itemView.setPicker,
				ResistanceSet.setsRange(),
				onPick = {
					model.setModel!!.value = it
					update()
				}
			)
			popupWindow.restorePickerState(model.setModel!!.value)
			popupWindow.showPopupMenu()
		}

		private fun onRep() {
			val repsRange = ResistanceSet.repsRange()
			val popupWindow = OnePickerPopupWindow(
				itemView.repPicker,
				repsRange,
				onPick = {
					model.repModel!!.value = it
					update()
				}
			)
			popupWindow.restorePickerState(model.repModel!!.value)
			popupWindow.showPopupMenu()
		}

		private fun onRM() {
			val repsRange = ResistanceSet.repsRange()
			val popupWindow = OnePickerPopupWindow(
				itemView.rmPicker,
				repsRange,
				onPick = {
					model.rmModel!!.value = it
					update()
				}
			)
			popupWindow.restorePickerState(model.rmModel!!.value)
			popupWindow.showPopupMenu()
		}

		private fun onTime() {
			val popupWindow = TimePickerPopupWindow(
				itemView.timePicker,
				listOf(ResistanceSet.minuteRange(), ResistanceSet.secondsRange()),
				onPick = {
					val minute = it[0].toString().toInt()
					val second = it[1].toString().toInt()
					val result = String.format("%02d:%02d", minute, second)
					if (model.repModel != null) { model.repModel!!.value = result }
					if (model.rmModel != null) { model.rmModel!!.value = result }
					update()
				}
			)

			var minute = "00"; var second = "00"
			if (model.repModel != null && (model.repModel!!.value as String).contains(":")) {
				minute = (model.repModel!!.value as String).split(":")[0]
				second = (model.repModel!!.value as String).split(":")[1]
			}
			if (model.rmModel != null && (model.rmModel!!.value as String).contains(":")) {
				minute = (model.rmModel!!.value as String).split(":")[0]
				second = (model.rmModel!!.value as String).split(":")[1]
			}
			popupWindow.restorePickerState(listOf(minute, second))
			popupWindow.showPopupMenu()
		}

		private fun onKg() {
			val popupWindow = MultiplePickerPopupWindow(
				itemView.kgPicker,
				listOf(ResistanceSet.kgsBigRange(), ResistanceSet.kgsSmallRange()),
				onPick = {
					val big = max(it[0].toString().toInt(), 0)
					val small = it[1].toString().toDouble()
					model.kgModel!!.value = big + small
					update()
				}
			)

			val value = model.kgModel!!.value
			var big = ResistanceSet.defaultKg().toInt()
			var small = 0.0
			if (value !is String) {
				big = (value as Double).toInt()
				small = value.toString().toDouble() - big.toDouble()
			}
			popupWindow.restorePickerState(listOf(big, small))
			popupWindow.showPopupMenu()
		}

		private fun onPercent() {
			val popupWindow = OnePickerPopupWindow(
				itemView.percentPicker,
				ResistanceSet.percentRange(),
				onPick = {
					model.percentModel!!.value = it
					update()
				}
			)
			popupWindow.restorePickerState(model.percentModel!!.value.toString() + "%")
			popupWindow.showPopupMenu()
		}
	}

	inner class MultipleSetListItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: SetViewModel
		fun setModel(model: SetViewModel) {
			this.model = model
			when (workoutMethod) {
				SAME_MUSCLE_SUPERSET, DIFFERENT_MUSCLE_SUPERSET -> {
					val set = model.superSet!!
					this.model.repsModel = ListValueViewModel(set.reps.mapIndexed { index, value -> ValueViewModel(value, index) })
					this.model.kgsModel = ListValueViewModel(set.kgs.mapIndexed { index, value: MutableList<Double> -> ValueViewModel(value[set.indexOfKg], index) })
				}
				else -> {
					val set = model.dropSet!!
					this.model.repsModel = ListValueViewModel(set.reps.mapIndexed { index, value -> ValueViewModel(value, index) })
					this.model.kgsModel = ListValueViewModel(set.kgs[set.indexOfKg].mapIndexed { index, value -> ValueViewModel(value, index) })
				}
			}
			init()
		}

		private fun init() {
			update()
		}

		private fun update() {
			with (itemView) {
				if (model.repsModel == null || model.kgsModel == null) { return }
				when (workoutMethod) {
					DROP_SET -> { repsTitleView.text = "Reps (Drops):" }
					else -> { repsTitleView.text = "Reps" }
				}

				repsRecyclerView.adapter = ValueListAdapter(model.repsModel!!)
				kgsRecyclerView.adapter = ValueListAdapter(model.kgsModel!!, true)

				if (kgOnly) {
					repsContainer.makeGone()
				}
			}
		}

		inner class ValueListAdapter(val valuesModel: ListValueViewModel, val requireMultiPicker: Boolean = false): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
				if (viewType == 1) { return KgValuesListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_edit_set_value_list_item_view, parent, false)) }
				return ValueListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_edit_set_value_list_item_view, parent, false))
			}

			override fun getItemCount(): Int {
				return valuesModel.models.size
			}

			override fun getItemViewType(position: Int): Int {
				return if (requireMultiPicker) 1 else 0
			}

			override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
				if (holder is ValueListItemViewHolder) { holder.setModel(valuesModel.models[position]) }
				if (holder is KgValuesListItemViewHolder) { holder.setModel(valuesModel.models[position]) }
			}

			inner class ValueListItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
				private lateinit var model: ValueViewModel
				fun setModel(model: ValueViewModel) {
					this.model = model
					init()
				}

				private fun init() {
					val range = ResistanceSet.repsRange()
					itemView.valuePickerView.setOnClickListener {
						val popupWindow = OnePickerPopupWindow(
							itemView.valuePickerView,
							range,
							onPick = {
								model.value = it
								update()
							}
						)
						popupWindow.restorePickerState(model.value.toString())
						popupWindow.showPopupMenu()
					}
					update()
				}

				private fun update() {
					if (workoutMethod.isSuperSet()) {
						itemView.valueTitleView.text = "Ex.${model.index+1}"
					}
					if (workoutMethod == DROP_SET) {
						itemView.valueTitleView.text = "Drop${model.index+1}"
					}
					itemView.pickerValueView.text = "${model.value}"
				}
			}

			inner class KgValuesListItemViewHolder(val view: View): RecyclerView.ViewHolder(view) {
				private lateinit var model: ValueViewModel
				fun setModel(model: ValueViewModel) {
					this.model = model
					init()
				}

				private fun init() {
					itemView.valuePickerView.setOnClickListener {
						val popupWindow = MultiplePickerPopupWindow(
							itemView.valuePickerView,
							listOf(ResistanceSet.kgsBigRange(), ResistanceSet.kgsSmallRange()),
							onPick = {
								val big = max(it[0].toString().toInt(), 0)
								val small = it[1].toString().toDouble()
								model.value = big + small
								update()
							}
						)

						val big = (model.value as Double).toInt()
						val small = (model.value as Double) - big.toDouble()
						popupWindow.restorePickerState(listOf(big, small))
						popupWindow.showPopupMenu()
					}
					update()
				}

				private fun update() {
					if (workoutMethod.isSuperSet()) {
						itemView.valueTitleView.text = "Ex.${model.index+1}"
					}
					if (workoutMethod == DROP_SET) {
						itemView.valueTitleView.text = "Drop${model.index+1}"
					}
					if (ResistanceSet.isPlaceHolderKg(model.value as Double)) {
						itemView.pickerValueView.text = "0.0"
					} else {
						itemView.pickerValueView.text = "${model.value}"
					}
				}
			}
		}
	}

	private var viewModels: List<SetViewModel>
	init {
		viewModels = exercise.sets.map { SetViewModel(it) }
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		if (viewType == 0) {
			return MultipleSetListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_edit_set_vertical_list_item_view, parent, false))
		} else {
			return SingleSetListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_edit_set_horizontal_list_item_view, parent, false))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is SingleSetListItemViewHolder) { holder.setModel(viewModels[position]) }
		if (holder is MultipleSetListItemViewHolder) { holder.setModel(viewModels[position]) }
	}

	override fun getItemCount(): Int {
		return viewModels.size
	}

	override fun getItemViewType(position: Int): Int {
		if (workoutMethod.isSuperSet() || workoutMethod == DROP_SET) { return 0 }
		else { return 1 }
	}

	fun save() {
		var modelIndex = 0
		for (index in exercise.sets.indices) {
			val model = viewModels[modelIndex]
			val workoutMethod = exercise.sets[index].workoutMethod()
			when (workoutMethod) {
				SAME_MUSCLE_SUPERSET, DIFFERENT_MUSCLE_SUPERSET -> {
					if (model.repsModel == null || model.kgsModel == null) { return }
					val set = exercise.sets[index] as SuperSet
					val reps = set.reps
					val kgs = set.kgs.map { it[set.indexOfKg] }.toMutableList()
					for (valueModelIndex in model.repsModel!!.models.indices) {
						val repModel = model.repsModel!!.models[valueModelIndex]
						reps.removeAt(repModel.index)
						reps.add(repModel.index, (repModel.value as String))

						val kgModel = model.kgsModel!!.models[valueModelIndex]
						kgs.removeAt(kgModel.index)
						kgs.add(kgModel.index, kgModel.value as Double)
					}
					(exercise.sets[index] as SuperSet).update(reps, kgs)
				}
				PYRAMID -> {
					if (model.repModel != null && model.kgModel != null) {
						val newRep = model.repModel!!.stringValue()
						val newKg = model.kgModel!!.doubleValue()
						(exercise.sets[index] as PyramidSet).update(newRep, newKg)
					}
				}
				PERCENT -> {
					if (model.rmModel != null && model.kgModel != null && model.percentModel != null) {
						val newRM = model.rmModel!!.stringValue()
						val newKg = model.kgModel!!.doubleValue()
						val newPercent = model.percentModel!!.value.toString().toInt()
						(exercise.sets[index] as PercentSet).update(newRM, newKg, newPercent)
					}
				}
				DROP_SET -> {
					if (model.repsModel == null || model.kgsModel == null) { return }
					val set = exercise.sets[index] as DropSet
					val reps = set.reps
					val kgs = set.kgs[set.indexOfKg]
					for (valueModelIndex in model.repsModel!!.models.indices) {
						val repModel = model.repsModel!!.models[valueModelIndex]
						reps.removeAt(repModel.index)
						reps.add(repModel.index, (repModel.value as String))

						val kgModel = model.kgsModel!!.models[valueModelIndex]
						kgs.removeAt(kgModel.index)
						kgs.add(kgModel.index, kgModel.value as Double)
					}
					(exercise.sets[index] as DropSet).update(reps, kgs)
				}
				else -> {
					if (model.setModel != null && model.repModel != null && model.kgModel != null) {
						val newSet = model.setModel!!.intValue()
						val newRep = model.repModel!!.stringValue()
						val newKg = model.kgModel!!.doubleValue()
						(exercise.sets[index] as DefaultSet).update(newSet, newRep, newKg)
					}
				}
			}

			modelIndex++
		}
	}
}