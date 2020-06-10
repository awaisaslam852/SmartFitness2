package com.axles.smartfitness.ui.cardio

import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.*
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.engine.program.ProgramManager
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.cardio.drag_and_drop.ItemMoveCallbackListener
import com.axles.smartfitness.ui.cardio.edit.CardioEditDialog
import com.axles.smartfitness.ui.cardio.subadapter.*
import com.axles.smartfitness.ui.dialogs.MultiplicationDialog
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.resistance.ResistanceTrainingFragment
import kotlinx.android.synthetic.main.cardio_activities_fragment.*
import kotlinx.android.synthetic.main.cardio_training_content.*
import kotlinx.android.synthetic.main.toolbar_cardio.*
import java.util.*

@Suppress("UNCHECKED_CAST")
class CardioActivitiesFragment : BaseFragment(R.layout.cardio_activities_fragment) {
	private val args: CardioActivitiesFragmentArgs by navArgs()

	private lateinit var runningAdapter: RunningAdapter
	private lateinit var runningHelper: ItemTouchHelper

	private lateinit var cyclingAdapter: CyclingAdapter
	private lateinit var cyclingHelper: ItemTouchHelper

	private lateinit var ellipticalAdapter: EllipticalAdapter
	private lateinit var ellipticalHelper: ItemTouchHelper

	private lateinit var rowingMachineAdapter: RowingMachineAdapter
	private lateinit var rowingMachineHelper: ItemTouchHelper

	private lateinit var swimmingAdapter: SwimmingAdapter
	private lateinit var swimmingHelper: ItemTouchHelper

	private lateinit var stairClimbingAdapter: StairClimbingAdapter
	private lateinit var stairClimbingHelper: ItemTouchHelper

	private var program = CardioProgram()
	private var selectedActivityType: CardioActivityType? = null
		set(value) {
			field = value
			if (program.activitiesByType(selectedActivityType!!).isEmpty()) {
				program.addEmptyActivity(selectedActivityType!!)
			}
			update()
		}

	override fun resolveArguments() {
		if (args.program != null) {
			program = args.program as CardioProgram
		}
	}

	override fun init() {
		activityTypesRecyclerView.adapter = CardioActivityTypeListAdapter(program,
			onSelect = {
				selectedActivityType = it
			}
		)
		setupAllSubAdapters()
		setupButtonsListeners()
	}

	override fun update() {
		if (program.isNew()) {
			topBarBackBtn.makeVisible()
			topBarDiscardBtn.makeGone()

			topBarSaveBtn.setTextColor(Helper.color(R.color.gray))
			topBarSaveBtn.isEnabled = false
		} else {
			topBarBackBtn.makeGone()
			topBarDiscardBtn.makeVisible()

			topBarSaveBtn.setTextColor(Helper.color(R.color.white))
			topBarSaveBtn.isEnabled = true
		}

		(activityTypesRecyclerView.adapter as CardioActivityTypeListAdapter).update()
		if (selectedActivityType == null || !program.hasType(selectedActivityType!!)) {
			if (!program.isNew()) {
				(activityTypesRecyclerView.adapter as CardioActivityTypeListAdapter).setSelectedActivityType(program.activities.keys.first())
				return
			}

			noActivityContainer.makeVisible()
			activitiesContainer.makeGone()
			return
		}

		activityTypeTitleView.text = selectedActivityType!!.shortTitle()

		noActivityContainer.makeGone()
		activitiesContainer.makeVisible()

		val selectedActivityType = this.selectedActivityType!!
		val selectedActivities = program.activitiesByType(selectedActivityType)

		clearHelpers()
		when (selectedActivityType) {
			RUNNING -> {
				recyclerViewCardioRows.adapter = runningAdapter
				runningHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			CYCLING -> {
				recyclerViewCardioRows.adapter = cyclingAdapter
				cyclingHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			ELLIPTICAL -> {
				recyclerViewCardioRows.adapter = ellipticalAdapter
				ellipticalHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			ROWING_MACHINE -> {
				recyclerViewCardioRows.adapter = rowingMachineAdapter
				rowingMachineHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			SWIMMING -> {
				recyclerViewCardioRows.adapter = swimmingAdapter
				swimmingHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			STAIR_CLIMBING -> {
				recyclerViewCardioRows.adapter = stairClimbingAdapter
				stairClimbingHelper.attachToRecyclerView(recyclerViewCardioRows)
			}
			else -> {}
		}
		(recyclerViewCardioRows.adapter as ActivityListAdapter).update()

		updateTotalTimeDistance()
	}

	private fun setupAllSubAdapters() {
		runningAdapter = RunningAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		runningHelper = ItemTouchHelper(ItemMoveCallbackListener(runningAdapter))

		cyclingAdapter = CyclingAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		cyclingHelper = ItemTouchHelper(ItemMoveCallbackListener(cyclingAdapter))

		ellipticalAdapter = EllipticalAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		ellipticalHelper = ItemTouchHelper(ItemMoveCallbackListener(ellipticalAdapter))

		rowingMachineAdapter = RowingMachineAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		rowingMachineHelper = ItemTouchHelper(ItemMoveCallbackListener(rowingMachineAdapter))

		swimmingAdapter = SwimmingAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		swimmingHelper = ItemTouchHelper(ItemMoveCallbackListener(swimmingAdapter))

		stairClimbingAdapter = StairClimbingAdapter(program, childFragmentManager, ::onEditActivity, ::update)
		stairClimbingHelper = ItemTouchHelper(ItemMoveCallbackListener(stairClimbingAdapter))
	}

	private fun updateTotalTimeDistance() {
		if (selectedActivityType == null) {
			totalTimeView.text = "00:00"
			totalDistanceView.text = "0.0"
			return
		}

		totalTimeView.text = program.totalTime(selectedActivityType!!).toTimerString()
		totalDistanceView.text = program.totalDistance(selectedActivityType!!).toString()
	}

	private fun setupButtonsListeners() {
		buttonDeleteActivity.setOnClickListener { onDeleteActivity() }
		buttonMultiplyData.setOnClickListener { onMultiply() }
		buttonAddNewRow.setOnClickListener { onAddNewActivity() }

		topBarBackBtn.setOnClickListener {
			findNavController().navigateUp()
		}

		topBarDiscardBtn.setOnClickListener { onDiscard() }

		topBarSaveBtn.setOnClickListener {
			ProgramManager.save(program)

			val action = CardioActivitiesFragmentDirections.toProgramFulfill(program)
			findNavController().navigate(action)
		}

		imageButtonHelpCardio.setOnClickListener {
			findNavController().navigate(R.id.cardioActivitiesToCardioHelp)
		}
	}

	private fun showEditDialog(cardioActivity: CardioActivity) {
		val dialog = CardioEditDialog(cardioActivity,
			didEdit = {
				update()
			}
		)
		dialog.show(childFragmentManager, "CardioActivitiesFragment.CardioEditDialog")
	}

	private fun clearHelpers() {
		cyclingHelper.attachToRecyclerView(null)
		runningHelper.attachToRecyclerView(null)
		ellipticalHelper.attachToRecyclerView(null)
		rowingMachineHelper.attachToRecyclerView(null)
		stairClimbingHelper.attachToRecyclerView(null)
		swimmingHelper.attachToRecyclerView(null)
	}

	private fun onDiscard() {
		if (ProgramManager.isSavedProgram(program)) {
			val dialog = YesNoDialog(getString(R.string.q_leave_this_page_withtout_saving)) { yes ->
				if (yes) {
					findNavController().navigateUp()
				}
			}
			dialog.show(childFragmentManager, ResistanceTrainingFragment.DISCARD_DIALOG)
			return
		}

		YesNoDialog(getString(R.string.are_you_sure_you_want_to_delete_this_program)) { yes ->
			if (yes) {
				findNavController().navigateUp()
			}
		}.show(childFragmentManager, "CardioActivitiesFragment.DiscardDialog")
	}

	private fun onDeleteActivity() {
		YesNoDialog(getString(R.string.are_you_sure_you_want_to_delete_this_activity)) { yes ->
			if (yes) {
				program.clear(selectedActivityType!!)
				update()
			}
		}.show(childFragmentManager, "CardioActivitiesFragment.DeleteActivityDialog")
	}

	private fun onAddNewActivity() {
		if (selectedActivityType == null) { return }

		val activities = program.activitiesByType(selectedActivityType!!)
		if (activities.isEmpty()) {
			program.addEmptyActivity(selectedActivityType!!)
			update()
			return
		}

		val lastActivity = activities.last()
		if (!lastActivity.isEdited()) {
			Helper.showErrorToast(requireActivity(), R.string.fill_at_least_one_value)
			return
		}

		program.addEmptyActivity(selectedActivityType!!)
		update()
	}

	private fun onEditActivity(activity: CardioActivity) {
		showEditDialog(activity)
	}

	private fun onMultiply() {
		if (selectedActivityType == null) { return }

		val lastActivity = program.lastActivity(selectedActivityType!!)
		if (lastActivity == null || !lastActivity.isEdited()) {
			Helper.showErrorToast(requireActivity(), R.string.fill_at_least_one_value)
			return
		}

		MultiplicationDialog { count ->
			program.multiply(selectedActivityType!!, count)
			update()
		}.show(childFragmentManager, "CardioActivitiesFragment.MultiplicationDialog")
	}
}