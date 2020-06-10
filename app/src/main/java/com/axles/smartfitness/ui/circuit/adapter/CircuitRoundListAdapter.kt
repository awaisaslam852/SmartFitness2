package com.axles.smartfitness.ui.circuit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.circuit.CircuitRound
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.cardio.edit.CardioEditDialog
import com.axles.smartfitness.ui.circuit.adapter.viewHoler.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.drag_and_drop.ItemMoveCallback
import com.axles.smartfitness.ui.drag_and_drop.ItemTouchHelperContract
import com.axles.smartfitness.ui.drag_and_drop.StartDragListener
import com.axles.smartfitness.ui.resistance.edit.ExerciseEditDialog
import com.axles.smartfitness.ui.resistance.edit.SelectTimeFragment
import kotlinx.android.synthetic.main.circuit_placeholder_item_view.view.*
import kotlinx.android.synthetic.main.circuit_round_list_title_item_view.view.*

open class CircuitRoundListAdapter(
	val program: CircuitProgram,
	val fragment: BaseFragment,
	val isEditable: Boolean = true,
	val onExerciseDetail: (Exercise) -> Unit,
	val onAddResistance: (() -> Unit)? = null,
	val onAddCardio: (() -> Unit)? = null,
	val onEmpty: (() -> Unit)? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperContract, StartDragListener {
	private var models = mutableListOf<Any>()
	private var itemTouchHelper: ItemTouchHelper? = null

	override fun getItemCount(): Int {
		return models.size
	}

	override fun getItemViewType(position: Int): Int {
		val model = models[position]
		return when (model) {
			is CircuitRoundViewModel -> 0
			is CircuitRoundItemViewModel -> {
				if (model.item.isResistanceExercise()) 1
				else {
					when (model.item.cardioActivityType()) {
						CardioActivityType.RUNNING -> 2
						CardioActivityType.CYCLING -> 3
						CardioActivityType.ELLIPTICAL -> 4
						CardioActivityType.ROWING_MACHINE -> 5
						CardioActivityType.STAIR_CLIMBING -> 6
						CardioActivityType.JUMPING_ROPE -> 7
						else -> 7
					}
				}
			}
			else -> 8
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			0 -> RoundHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_round_list_title_item_view, parent, false))
			1 -> ResistanceExerciseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_exercise_list_item_view, parent, false), fragment.childFragmentManager, this)
			2 -> CardioRunningViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_running_activity_list_item, parent, false), this)
			3 -> CardioCyclingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_cycling_activity_list_item, parent, false), this)
			4 -> CardioEllipticalViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_elliptical_activity_list_item, parent, false), this)
			5 -> CardioRowingMachineViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_rowing_machine_activity_list_item, parent, false), this)
			6 -> CardioStairClimbingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_stair_climbing_activity_list_item, parent, false), this)
			7 -> CardioJumpingRopeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_jumping_rope_activity_list_item, parent, false), this)
			else -> PlaceHolderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circuit_placeholder_item_view, parent, false))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is RoundHeaderViewHolder) { holder.setModel(models[position] as CircuitRoundViewModel) }
		if (holder is CircuitRoundItemListViewHolder) { holder.bind(models[position] as CircuitRoundItemViewModel) }
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		super.onAttachedToRecyclerView(recyclerView)

		update()

		val itemMoveCallback = ItemMoveCallback(this)
		itemTouchHelper = ItemTouchHelper(itemMoveCallback)
		itemTouchHelper?.attachToRecyclerView(recyclerView)
	}

	override fun onRowMoved(fromPosition: Int, toPosition: Int) {
		if (toPosition == 0 || toPosition >= models.size-1) { return }
		val a = models[fromPosition]
		val b = models[toPosition]
		models.removeAt(fromPosition); models.add(fromPosition, b)
		models.removeAt(toPosition); models.add(toPosition, a)

		applyChanges()

		notifyItemMoved(fromPosition, toPosition)
	}

	override fun onRowSelected(viewHolder: RecyclerView.ViewHolder?) {
		if (viewHolder is CircuitRoundItemListViewHolder) {
			viewHolder.applyDragging(true)
		}
	}

	override fun onRowClear(viewHolder: RecyclerView.ViewHolder?) {
		if (viewHolder is CircuitRoundItemListViewHolder) {
			viewHolder.applyDragging(false)
		}
	}

	override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
		itemTouchHelper?.startDrag(viewHolder)
	}

	fun update() {
		models.clear()
		val rounds = program.rounds()
		for (roundIndex in rounds.indices) {
			val headerModel = CircuitRoundViewModel(roundIndex, rounds[roundIndex])
			models.add(headerModel)

			val items = rounds[roundIndex]
			for (itemIndex in items.indices) {
				models.add(CircuitRoundItemViewModel(headerModel, itemIndex, items[itemIndex]))
			}
		}
		if (isEditable) { models.add(CircuitRoundPlaceHolderModel()) }

		notifyDataSetChanged()
	}

	private fun applyChanges() {
		program.reset()
		for (roundIndex in models.indices) {
			if (models[roundIndex] !is CircuitRoundViewModel) { continue }
			val round = (models[roundIndex] as CircuitRoundViewModel).round
			round.clear()
			for (itemIndex in roundIndex+1 until models.size) {
				if (models[itemIndex] !is CircuitRoundItemViewModel) { break }
				val item = (models[itemIndex] as CircuitRoundItemViewModel).item
				round.addItem(item)
			}
			program.addRound(round)
		}
	}

	private fun didDeleteRoundItem(itemModel: CircuitRoundItemViewModel) {
		models.remove(itemModel)
		notifyItemRemoved(models.indexOf(itemModel))
		Helper.showToast(fragment.requireActivity(), R.string.exercise_deleted)
	}

	fun onDeleteRoundItem(itemModel: CircuitRoundItemViewModel) {
		if (!isEditable) return

		val roundModel = itemModel.roundModel
		if (roundModel.round.size <= 1) {
			onDeleteRound(roundModel)
			return
		}

		val dialog = YesNoDialog(Helper.string(R.string.q_want_to_delete_this_exercise), yesNoListener = { yes ->
			if (yes) {
				program.deleteItem(roundModel.round, itemModel.item)
				didDeleteRoundItem(itemModel)
			}
		})
		dialog.show(fragment.childFragmentManager, "CircuitTrainingFragment.DeleteExercise.YesNoDialog")
	}

	private fun didDeleteRound(roundModel: CircuitRoundViewModel) {
		if (program.isNew()) {
			onEmpty?.invoke()
			return
		}

		val roundIndex = models.indexOf(roundModel)
		val removedModels = mutableListOf<Any>(roundModel)
		for (index in roundIndex+1 until models.size) {
			if (models[index] !is CircuitRoundItemViewModel) { break }
			removedModels.add(models[index] as CircuitRoundItemViewModel)
		}
		models.removeAll(removedModels)
		notifyItemRangeRemoved(roundIndex, models.size)

		for (index in roundIndex until models.size) {
			val model = models[index]
			if (model !is CircuitRoundViewModel) { continue }
			model.roundIndex = model.roundIndex-1
			notifyItemChanged(index)
		}

		Helper.showToast(fragment.requireActivity(), R.string.round_deleted)
	}

	fun onDeleteRound(roundModel: CircuitRoundViewModel) {
		if (!isEditable) return

		val dialog = YesNoDialog(Helper.string(R.string.q_want_to_delete_this_round), yesNoListener = { yes ->
			if (yes) {
				program.deleteRound(roundModel.round)
				didDeleteRound(roundModel)
			}
		})
		dialog.show(fragment.childFragmentManager, "CircuitTrainingFragment.DeleteRound.YesNoDialog")
	}

	fun onEdit(itemModel: CircuitRoundItemViewModel) {
		if (!isEditable) return

		if (itemModel.item.isResistanceExercise()) onEditResistance(itemModel)
		if (itemModel.item.isCardioActivity()) onEditCardio(itemModel)
	}

	private fun onEditResistance(itemModel: CircuitRoundItemViewModel) {
		val dialog = ExerciseEditDialog(
			ProgramType.CIRCUIT,
			itemModel.item.resistanceExercise,
			didSave = {
				notifyItemChanged(models.indexOf(itemModel))
			}
		)
		dialog.show(fragment.childFragmentManager, "ExercisesListFragment.ExerciseEditDialog")
	}

	private fun onEditCardio(itemModel: CircuitRoundItemViewModel) {
		val dialog = CardioEditDialog(
			itemModel.item.cardioActivity,
			didEdit = {
				notifyItemChanged(models.indexOf(itemModel))
			}
		)
		dialog.show(fragment.childFragmentManager, "CircuitTrainingFragment.CardioEditDialog")
	}

	fun onExerciseDetail(exercise: Exercise) {
		this.onExerciseDetail.invoke(exercise)
	}

	inner class RoundHeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var roundModel: CircuitRoundViewModel
		private lateinit var round: CircuitRound

		fun setModel(model: CircuitRoundViewModel) {
			this.roundModel = model
			this.round = model.round
			init()
		}

		private fun init() {
			with(itemView) {
				deleteBtn.setOnClickListener { onDeleteRound(roundModel) }
				restContainer.setOnClickListener {
					val prevRound = program.rounds()[roundModel.roundIndex-1]
					val dialog = SelectTimeFragment(
						prevRound.restDuration,
						onSave = {
							prevRound.restDuration = it
							this@RoundHeaderViewHolder.update()
						}
					)
					dialog.show(fragment.childFragmentManager, "ExercisesListFragment.SelectTimeFragment")
				}
			}
			update()
		}

		private fun update() {
			with(itemView) {
				deleteBtn.makeVisibleOrGone(isEditable)
				titleView.text = "ROUND ${roundModel.roundIndex+1}"
				if (roundModel.roundIndex <= 0) {
					restContainer.makeGone()
				} else {
					restContainer.makeVisible()
					val prevRound = program.rounds()[roundModel.roundIndex-1]
					restDurationView.text = prevRound.restDuration()
				}
			}
		}
	}

	inner class PlaceHolderViewHolder(view: View): RecyclerView.ViewHolder(view) {
		init {
			with (itemView) {
				addNewResistanceButton.setOnClickListener { onAddResistance?.invoke() }
				addNewCardioButton.setOnClickListener { onAddCardio?.invoke() }

				val rounds = program.rounds()
				if (rounds.size > 0 && rounds.last().isEmpty()) {
					addExerciseTitleView.text = Helper.string(R.string.add_first_exercise)
				} else {
					addExerciseTitleView.text = Helper.string(R.string.add_new_exercise)
				}
			}
		}
	}
}