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
import com.axles.smartfitness.engine.cardio.SwimmingActivity
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.cardio.adapter_behaviour.*
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.swimming_activity_list_item.view.*
import java.util.*

class SwimmingAdapter(program: CardioProgram,
					  fragmentManager: FragmentManager,
					  onEdit: (CardioActivity) -> Unit,
					  didUpdate: () -> Unit
): ActivityListAdapter(program, CardioActivityType.SWIMMING, fragmentManager, onEdit, didUpdate), DragAndDropBehaviour<SwimmingActivity> {

	private var activities = mutableListOf<SwimmingActivity>()

	init {
		activities = program.activitiesByType(activityType).map { it as SwimmingActivity }.toMutableList()
	}

	override fun getDragAndDropModel(): MutableList<SwimmingActivity> {
		return activities
	}

	override fun notifyItems(fromPosition: Int, toPosition: Int) {
		notifyItemMoved(fromPosition, toPosition)
		logD("items moved from:$fromPosition to:$toPosition")
	}

	override fun update() {
		activities = program.activitiesByType(activityType).map { it as SwimmingActivity }.toMutableList()
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwimmingViewHolder {
		return SwimmingViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.swimming_activity_list_item, parent, false))
	}

	override fun getItemCount() = activities.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as SwimmingViewHolder).setActivity(activities[position])
	}

	inner class SwimmingViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activity: SwimmingActivity
		fun setActivity(activity: SwimmingActivity) {
			this.activity = activity
			init()
		}

		private fun init() {
			with (itemView) {
				itemView.imageButtonDeleteActivity.setOnClickListener { onDelete() }
				itemView.imageButtonEditMenu.setOnClickListener { onEdit.invoke(activity) }
				itemView.cardViewStairsItem.setOnClickListener { onEdit.invoke(activity) }
			}
			update()
		}

		private fun update() {
			with (itemView) {
				textViewIntensity.text = context.getString(activity.intensity.swimmingIntensityRes)
				textViewExercise.text = context.getString(activity.exercise.swimmingExerciseRes)
				textViewStyle.text = context.getString(activity.style.styleRes)

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