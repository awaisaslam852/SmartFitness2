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
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.cardio.CyclingActivity
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.cycling_activity_list_item.view.*
import java.util.*

class CyclingAdapter(
	program: CardioProgram,
	fragmentManager: FragmentManager,
	onEdit: (CardioActivity) -> Unit,
	didUpdate: () -> Unit
): ActivityListAdapter(program, CardioActivityType.CYCLING, fragmentManager, onEdit, didUpdate), DragAndDropBehaviour<CyclingActivity> {

	private var activities = mutableListOf<CyclingActivity>()

	init {
		activities = program.activitiesByType(activityType).map { it as CyclingActivity }.toMutableList()
	}

	fun activities(): List<CardioActivity> {
		return activities
	}

	override fun getDragAndDropModel(): MutableList<CyclingActivity> {
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
		activities = program.activitiesByType(activityType).map { it as CyclingActivity }.toMutableList()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CyclingViewHolder {
		return CyclingViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.cycling_activity_list_item, parent, false))
	}

	override fun getItemCount() = activities.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as CyclingViewHolder).setActivity(activities[position])
	}

	inner class CyclingViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activity: CyclingActivity
		fun setActivity(activity: CyclingActivity) {
			this.activity = activity
			init()
		}

		private fun init() {
			with (itemView) {
				imageButtonDeleteActivity.setOnClickListener { onDelete() }
				imageButtonEditMenu.setOnClickListener { onEdit.invoke(activity) }
				cardViewCyclingItem.setOnClickListener { onEdit.invoke(activity) }
			}

			update()
		}

		private fun update() {
			with(itemView) {
				textViewResistance.text = activity.resistance.toString()
				val rpm = "${activity.rpmFirst} - ${activity.rpmSecond}"
				textViewRPM.text = rpm

				when(activity.valueType){
					ValueType.TIME -> {
						itemView.imageViewTimeDistanceIcon.setImageResource(R.drawable.icon_clock)
						itemView.textViewTimeDistance.text = activity.timeSeconds.toTimerString()
					}

					ValueType.DISTANCE -> {
						itemView.imageViewTimeDistanceIcon.setImageResource(R.drawable.icon_destinations)
						itemView.textViewTimeDistance.text =  String.format(Locale.UK,"%.2f", activity.distance)
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