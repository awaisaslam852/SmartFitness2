package com.axles.smartfitness.ui.program.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.cardio.*
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.engine.cardio.CardioActivity.ValueType.*
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.extensions.toTimerString
import kotlinx.android.synthetic.main.program_detail_cardio_item_view.view.*
import kotlinx.android.synthetic.main.program_detail_cardio_activity_list_item_view.view.*
import java.util.*

class CardioAdapter(
	val program: CardioProgram
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	class ActivityViewModel(val activity: CardioActivity, var isSelected: Boolean = false)
	private var activityType: CardioActivityType
	private lateinit var valueType: CardioActivity.ValueType

	init {
		activityType = program.activities.keys.first()
	}

	fun setActivityType(activityType: CardioActivityType) {
		this.activityType = activityType
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return TypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_detail_cardio_item_view, parent, false))
	}

	override fun getItemCount(): Int {
		return 1
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as TypeViewHolder).setActivityType(activityType)
	}

	inner class TypeViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activityType: CardioActivityType
		fun setActivityType(activityType: CardioActivityType) {
			this.activityType = activityType
			init()
		}

		private fun init() {
			update()
		}

		private fun update() {
			with (itemView) {
				titleView.text = activityType.shortTitle()

				listOf(headerTimeDistanceContainer,
					headerSpeedContainer,
					headerResistanceContainer,
					headerInclineContainer,
					headerStyleContainer,
					headerExerciseContainer,
					headerIntensityContainer,
					headerPaceContainer,
					headerStepsContainer,
					headerSpmContainer,
					headerRpmContainer
				).forEach { it.makeGone() }

				when (activityType) {
					RUNNING -> {
						headerTimeDistanceContainer.makeVisible()
						headerSpeedContainer.makeVisible()
						headerInclineContainer.makeVisible()
						headerPaceContainer.makeVisible()
					}
					CYCLING -> {
						headerTimeDistanceContainer.makeVisible()
						headerResistanceContainer.makeVisible()
						headerRpmContainer.makeVisible()
					}
					ELLIPTICAL -> {
						headerTimeDistanceContainer.makeVisible()
						headerResistanceContainer.makeVisible()
						headerInclineContainer.makeVisible()
						headerSpmContainer.makeVisible()
					}
					ROWING_MACHINE -> {
						headerTimeDistanceContainer.makeVisible()
						headerIntensityContainer.makeVisible()
						headerPaceContainer.makeVisible()
						headerSpmContainer.makeVisible()
					}
					SWIMMING -> {
						headerTimeDistanceContainer.makeVisible()
						headerStyleContainer.makeVisible()
						headerExerciseContainer.makeVisible()
						headerIntensityContainer.makeVisible()
					}
					STAIR_CLIMBING -> {
						headerTimeDistanceContainer.makeVisible()
						headerResistanceContainer.makeVisible()
						headerStepsContainer.makeVisible()
						headerSpmContainer.makeVisible()
					}
				}

				valueType = program.valueTypeBy(activityType)
				when (valueType) {
					TIME -> {
						headerTimeDistanceView.text = Helper.string(R.string.time)
						headerSmallDistanceView.makeGone()

						totalTimeView.makeVisible()
						totalTimeView.text = "${Helper.string(R.string.total_time)} ${program.totalTime(activityType).toTimerString()}"
						totalDistanceView.makeGone()
					}
					DISTANCE -> {
						headerTimeDistanceView.text = Helper.string(R.string.distance)
						headerSmallDistanceView.makeGone()

						totalTimeView.makeGone()
						totalDistanceView.makeVisible()
						totalDistanceView.text = "${Helper.string(R.string.total_distance)} ${program.totalDistance(activityType)}"
					}
					else -> {
						headerTimeDistanceView.text = Helper.string(R.string.time)
						headerSmallDistanceView.makeVisible()

						totalTimeView.makeVisible()
						totalDistanceView.makeVisible()
						totalTimeView.text = "${Helper.string(R.string.total_time)} ${program.totalTime(activityType).toTimerString()}"
						totalDistanceView.text = "${Helper.string(R.string.total_distance)} ${program.totalDistance(activityType)}"
					}
				}

				activitiesRecyclerView.adapter = ActivityAdapter(program.activitiesByType(activityType))
			}
		}
	}

	inner class ActivityAdapter(
		val activities: List<CardioActivity>
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		private var models: List<ActivityViewModel> = activities.map { ActivityViewModel(it) }

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return ActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_detail_cardio_activity_list_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return models.size
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is ActivityViewHolder) {
				holder.setModel(models[position])
				holder.itemView.setOnClickListener {
					models[position].isSelected = !models[position].isSelected
					notifyItemChanged(position)
				}
			}
		}
	}

	inner class ActivityViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: ActivityViewModel
		private lateinit var activity: CardioActivity
		fun setModel(model: ActivityViewModel) {
			this.model = model
			this.activity = model.activity
			init()
		}

		private fun init() {
			update()
		}

		private fun update() {
			with (itemView) {
				if (absoluteAdapterPosition % 2 == 0) {
					setBackgroundResource(R.color.white)
				} else {
					setBackgroundResource(R.color.gray)
				}

				listOf(timeDistanceContainer,
					speedContainer,
					resistanceContainer,
					inclineContainer,
					styleContainer,
					exerciseContainer,
					intensityContainer,
					paceContainer,
					stepsContainer,
					spmContainer,
					rpmContainer
				).forEach { it.makeGone() }

				when (activity) {
					is RunningActivity -> {
						timeDistanceContainer.makeVisible()
						speedContainer.makeVisible()
						inclineContainer.makeVisible()
						paceContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as RunningActivity
						speedView.text = String.format(Locale.UK,"%.1f", activity.speed)
						inclineView.text = String.format(Locale.UK,"%.1f", activity.incline)
						paceView.text = activity.paceSeconds.toTimerString()

						when (valueType){
							NONE -> {
								timeDistanceIconContainer.makeVisible()
								when (activity.valueType) {
									TIME -> {
										timeIconView.makeVisible()
										distanceIconView.makeGone()
										timeDistanceView.text = activity.timeSeconds.toTimerString()
									}
									DISTANCE -> {
										timeIconView.makeGone()
										distanceIconView.makeVisible()
										timeDistanceView.text = String.format(Locale.UK,"%.2f", activity.distance)
									}
								}
							}
							TIME -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
							}
							DISTANCE -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "0.0 - " + String.format(Locale.UK,"%.2f", activity.distance)
							}
						}
					}
					is CyclingActivity -> {
						timeDistanceContainer.makeVisible()
						resistanceContainer.makeVisible()
						rpmContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as CyclingActivity
						resistanceView.text = activity.resistance.toString()
						val rpm = "${activity.rpmFirst} - ${activity.rpmSecond}"
						rpmView.text = rpm

						when (valueType){
							NONE -> {
								timeDistanceIconContainer.makeVisible()
								when (activity.valueType) {
									TIME -> {
										timeIconView.makeVisible()
										distanceIconView.makeGone()
										timeDistanceView.text = activity.timeSeconds.toTimerString()
									}
									DISTANCE -> {
										timeIconView.makeGone()
										distanceIconView.makeVisible()
										timeDistanceView.text = String.format(Locale.UK,"%.2f", activity.distance)
									}
								}
							}
							TIME -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
							}
							DISTANCE -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "0.0 - " + String.format(Locale.UK,"%.2f", activity.distance)
							}
						}
					}
					is EllipticalActivity -> {
						timeDistanceContainer.makeVisible()
						resistanceContainer.makeVisible()
						inclineContainer.makeVisible()
						spmContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as EllipticalActivity
						resistanceView.text = activity.resistance.toString()
						inclineView.text = String.format(Locale.UK,"%.1f", activity.incline)
						val spm = "${activity.spmFirst} - ${activity.spmSecond}"
						spmView.text = spm

						when (valueType){
							NONE -> {
								timeDistanceIconContainer.makeVisible()
								when (activity.valueType) {
									TIME -> {
										timeIconView.makeVisible()
										distanceIconView.makeGone()
										timeDistanceView.text = activity.timeSeconds.toTimerString()
									}
									DISTANCE -> {
										timeIconView.makeGone()
										distanceIconView.makeVisible()
										timeDistanceView.text = String.format(Locale.UK,"%.2f", activity.distance)
									}
								}
							}
							TIME -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
							}
							DISTANCE -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "0.0 - " + String.format(Locale.UK,"%.2f", activity.distance)
							}
						}
					}
					is RowingMachineActivity -> {
						timeDistanceContainer.makeVisible()
						intensityContainer.makeVisible()
						paceContainer.makeVisible()
						spmContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as RowingMachineActivity
						intensityView.text = itemView.context.getString(activity.intensity.intensityRes)
						paceView.text = activity.paceSeconds.toTimerString()
						val spm = "${activity.spmFirst} - ${activity.spmSecond}"
						spmView.text = spm

						when (valueType){
							NONE -> {
								timeDistanceIconContainer.makeVisible()
								when (activity.valueType) {
									TIME -> {
										timeIconView.makeVisible()
										distanceIconView.makeGone()
										timeDistanceView.text = activity.timeSeconds.toTimerString()
									}
									DISTANCE -> {
										timeIconView.makeGone()
										distanceIconView.makeVisible()
										timeDistanceView.text = String.format(Locale.UK,"%.2f", activity.distance)
									}
								}
							}
							TIME -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
							}
							DISTANCE -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "0.0 - " + String.format(Locale.UK,"%.2f", activity.distance)
							}
						}
					}
					is SwimmingActivity -> {
						timeDistanceContainer.makeVisible()
						styleContainer.makeVisible()
						exerciseContainer.makeVisible()
						intensityContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as SwimmingActivity
						intensityView.text = context.getString(activity.intensity.swimmingIntensityRes)
						exerciseView.text = context.getString(activity.exercise.swimmingExerciseRes)
						styleView.text = context.getString(activity.style.styleRes)

						when (valueType){
							NONE -> {
								timeDistanceIconContainer.makeVisible()
								when (activity.valueType) {
									TIME -> {
										timeIconView.makeVisible()
										distanceIconView.makeGone()
										timeDistanceView.text = activity.timeSeconds.toTimerString()
									}
									DISTANCE -> {
										timeIconView.makeGone()
										distanceIconView.makeVisible()
										timeDistanceView.text = String.format(Locale.UK,"%.2f", activity.distance)
									}
								}
							}
							TIME -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
							}
							DISTANCE -> {
								timeDistanceIconContainer.makeGone()
								timeDistanceView.text = "0.0 - " + String.format(Locale.UK,"%.2f", activity.distance)
							}
						}
					}
					is StairClimbingActivity -> {
						timeDistanceContainer.makeVisible()
						resistanceContainer.makeVisible()
						stepsContainer.makeVisible()
						spmContainer.makeVisible()

						val activity = this@ActivityViewHolder.activity as StairClimbingActivity
						resistanceView.text = activity.resistance.toString()
						stepsView.text = activity.steps.toString()
						val spm = "${activity.spmFirst} - ${activity.spmSecond}"
						spmView.text = spm

						timeDistanceIconContainer.makeGone()
						timeDistanceView.text = "00:00 - " + activity.timeSeconds.toTimerString()
					}
				}

				selectionIndicator.makeVisibleOrGone(model.isSelected)
			}
		}
	}
}