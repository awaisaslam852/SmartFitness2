package com.axles.smartfitness.ui.cardio.subadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.cardio.StairClimbingActivity
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.stair_climbing_activity_list_item.view.*

class StairClimbingAdapter(program: CardioProgram,
						   fragmentManager: FragmentManager,
						   onEdit: (CardioActivity) -> Unit,
						   didUpdate: () -> Unit
): ActivityListAdapter(program, CardioActivityType.STAIR_CLIMBING, fragmentManager, onEdit, didUpdate), DragAndDropBehaviour<StairClimbingActivity>{

	private var activities = mutableListOf<StairClimbingActivity>()

	init {
		activities = program.activitiesByType(activityType).map { it as StairClimbingActivity }.toMutableList()
	}

	override fun getDragAndDropModel(): MutableList<StairClimbingActivity> {
		return activities
	}

	override fun notifyItems(fromPosition: Int, toPosition: Int) {
		notifyItemMoved(fromPosition, toPosition)
		logD("items moved from:$fromPosition to:$toPosition")
	}

	override fun update() {
		activities = program.activitiesByType(activityType).map { it as StairClimbingActivity }.toMutableList()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StairClimbingViewHolder {
		return StairClimbingViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.stair_climbing_activity_list_item, parent, false))
	}

	override fun getItemCount() = activities.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as StairClimbingViewHolder).setActivity(activities[position])
	}

	inner class StairClimbingViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activity: StairClimbingActivity
		fun setActivity(activity: StairClimbingActivity) {
			this.activity = activity
			init()
		}

		private fun init() {
			with (itemView) {
				imageButtonDeleteActivity.setOnClickListener { onDelete() }
				imageButtonEditMenu.setOnClickListener { onEdit.invoke(activity) }
				cardViewStairsItem.setOnClickListener { onEdit.invoke(activity) }
			}
			update()
		}

		private fun update() {
			with (itemView) {
				textViewResistance.text = activity.resistance.toString()
				textViewSteps.text = activity.steps.toString()
				val spm = "${activity.spmFirst} - ${activity.spmSecond}"
				textViewSpm.text = spm

				imageViewTimeDistanceIcon.setImageResource(R.drawable.icon_clock)
				textViewTimeDistance.text = activity.timeSeconds.toTimerString()
			}
		}

		private fun onDelete() {
			if (activities.size == 1) {
				YesNoDialog(Helper.string(R.string.are_you_sure_you_want_to_delete_this_activity)){ yes->
					if (yes) {
						program.clear(activityType)
						didUpdate.invoke()
					}
				}.show(fragmentManager, "tag")
				return
			}

			program.deleteActivity(activityType, activity)
			notifyDataSetChanged()
		}
	}
}