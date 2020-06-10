package com.axles.smartfitness.ui.cardio.subadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.CardioActivity.*
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.cardio.RowingMachineActivity
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.rowing_machine_activity_list_item.view.*
import java.util.*

class RowingMachineAdapter(program: CardioProgram,
						   fragmentManager: FragmentManager,
						   onEdit: (CardioActivity) -> Unit,
						   didUpdate: () -> Unit
): ActivityListAdapter(program, CardioActivityType.ROWING_MACHINE, fragmentManager, onEdit, didUpdate), DragAndDropBehaviour<RowingMachineActivity> {

	private var activities = mutableListOf<RowingMachineActivity>()

	init {
		activities = program.activitiesByType(activityType).map { it as RowingMachineActivity }.toMutableList()
	}

	override fun getDragAndDropModel(): MutableList<RowingMachineActivity> {
		return activities
	}

	override fun notifyItems(fromPosition: Int, toPosition: Int) {
		notifyItemMoved(fromPosition, toPosition)
		logD("items moved from:$fromPosition to:$toPosition")
	}

	override fun update() {
		activities = program.activitiesByType(activityType).map { it as RowingMachineActivity }.toMutableList()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowingMachineViewHolder {
		return RowingMachineViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.rowing_machine_activity_list_item, parent, false))
	}

	override fun getItemCount() = activities.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as RowingMachineViewHolder).setActivity(activities[position])
	}

	inner class RowingMachineViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activity: RowingMachineActivity
		fun setActivity(activity: RowingMachineActivity) {
			this.activity = activity
			init()
		}

		private fun init() {
			with (itemView) {
				imageButtonDeleteActivity.setOnClickListener { onDelete() }
				imageButtonEditMenu.setOnClickListener { onEdit.invoke(activity) }
				cardViewRowingMachineItem.setOnClickListener { onEdit.invoke(activity) }
			}
			update()
		}

		private fun update() {
			with (itemView) {
				textViewIntensity.text = context.getString(activity.intensity.intensityRes)
				textViewPace.text = activity.paceSeconds.toTimerString()
				val spm = "${activity.spmFirst} - ${activity.spmSecond}"
				textViewSpm.text = spm

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