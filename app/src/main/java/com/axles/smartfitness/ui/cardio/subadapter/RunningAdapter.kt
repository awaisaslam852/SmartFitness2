package com.axles.smartfitness.ui.cardio.subadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.CardioActivity.*
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.cardio.RunningActivity
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.running_activity_list_item.view.*
import java.util.*

class RunningAdapter(
	program: CardioProgram,
	fragmentManager: FragmentManager,
	onEdit: (CardioActivity) -> Unit,
	didUpdate: () -> Unit
): ActivityListAdapter(program, RUNNING, fragmentManager, onEdit, didUpdate), DragAndDropBehaviour<RunningActivity> {

	private var activities = mutableListOf<RunningActivity>()

	init {
		activities = program.activitiesByType(activityType).map { it as RunningActivity }.toMutableList()
	}

	override fun getDragAndDropModel(): MutableList<RunningActivity> {
		return activities
	}

	override fun notifyItems(fromPosition: Int, toPosition: Int) {
		notifyItemMoved(fromPosition, toPosition)
		logD("items moved from:$fromPosition to:$toPosition")
	}

	override fun canBeMoved(fromPosition: Int, toPosition: Int): Boolean {
		return (activities[fromPosition].isEdited() && activities[toPosition].isEdited())
	}

	override fun update() {
		activities = program.activitiesByType(activityType).map { it as RunningActivity }.toMutableList()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return RunningViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.running_activity_list_item, parent, false))
	}

	override fun getItemCount() = activities.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as RunningViewHolder).setActivity(activities[position])
	}

	inner class RunningViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activity: RunningActivity
		fun setActivity(activity: RunningActivity) {
			this.activity = activity
			init()
		}

		private fun init() {
			with (itemView) {
				imageButtonDeleteActivity.setOnClickListener { onDelete() }
				imageButtonEditMenu.setOnClickListener { onEdit.invoke(activity) }
				cardViewRunningItem.setOnClickListener { onEdit.invoke(activity) }
			}
			update()
		}

		private fun update() {
			with (itemView) {
				textViewSpeed.text = String.format(Locale.UK,"%.1f", activity.speed)
				textViewIncline.text = String.format(Locale.UK,"%.1f", activity.incline)
				textViewPace.text = activity.paceSeconds.toTimerString()

				when(activity.valueType){
					ValueType.TIME -> {
						imageViewTimeDistanceIcon.setImageResource(R.drawable.icon_clock)
						textViewTimeDistance.text = activity.timeSeconds.toTimerString()
					}

					ValueType.DISTANCE -> {
						imageViewTimeDistanceIcon.setImageResource(R.drawable.icon_destinations)
						textViewTimeDistance.text =  String.format(Locale.UK,"%.2f", activity.distance)
					}
				}
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