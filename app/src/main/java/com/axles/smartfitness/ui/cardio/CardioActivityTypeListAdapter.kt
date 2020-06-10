package com.axles.smartfitness.ui.cardio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.cardio_activity_list_item.view.*

class CardioActivityTypeListAdapter(
	val program: CardioProgram,
	val onSelect: (CardioActivityType) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var activityTypes: List<CardioActivityType>
	private lateinit var selectedActivityType: CardioActivityType

	init {
		val selectedTypes = program.activities.keys
		val noneSelectedTypes = listOf(RUNNING, CYCLING, ELLIPTICAL, ROWING_MACHINE, STAIR_CLIMBING, SWIMMING).filter { !selectedTypes.contains(it) }.sortedBy { it.index }
		activityTypes = (selectedTypes + noneSelectedTypes).toList()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardio_activity_list_item, parent, false))

	override fun getItemCount() = activityTypes.size

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) {
			holder.setActivityType(activityTypes[position])
			holder.itemView.setOnClickListener {
				selectedActivityType = activityTypes[position]
				onSelect.invoke(selectedActivityType)
				notifyDataSetChanged()
			}
		}
	}

	fun setSelectedActivityType(activityType: CardioActivityType) {
		selectedActivityType = activityType
		onSelect.invoke(selectedActivityType)
		notifyDataSetChanged()
	}

	fun update() {
		val selectedTypes = program.activities.keys
		val noneSelectedTypes = listOf(RUNNING, CYCLING, ELLIPTICAL, ROWING_MACHINE, STAIR_CLIMBING, SWIMMING).filter { !selectedTypes.contains(it) }.sortedBy { it.index }
		activityTypes = (selectedTypes + noneSelectedTypes).toList()
		notifyDataSetChanged()
	}

	inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var activityType: CardioActivityType
		fun setActivityType(activityType: CardioActivityType) {
			this.activityType = activityType
			init()
		}

		private fun init() {
			with (itemView) {
			}
			update()
		}

		private fun update() {
			with (itemView) {
				textViewActivityName.text = activityType.shortTitle()
				textViewActivityTitleActivated.text = activityType.title()

				Glide.with(context)
					.load(activityType.iconResId())
					.optionalFitCenter()
					.override(Target.SIZE_ORIGINAL)
					.into(imageViewActivityIcon)


				Glide.with(context)
					.load(activityType.iconResId())
					.optionalFitCenter()
					.override(Target.SIZE_ORIGINAL)
					.into(imageViewIconActivated)

				if (!program.hasType(activityType)) {
					inactiveContainer.makeVisible()
					activatedContainer.makeGone()
				} else {
					inactiveContainer.makeGone()
					activatedContainer.makeVisible()

					if (this@CardioActivityTypeListAdapter::selectedActivityType.isInitialized && activityType == selectedActivityType) {
						activatedContainer.background = context.getDrawable(R.drawable.blue_thin_stroke_background)
					} else {
						activatedContainer.background = context.getDrawable(R.color.white)
					}
				}
			}
		}
	}
}