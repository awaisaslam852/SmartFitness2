package com.axles.smartfitness.ui.program.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import kotlinx.android.synthetic.main.program_detail_cardio_type_list_item.view.*

class CardioActivityTypeAdapter(
	val program: CardioProgram,
	val onChoose: (CardioActivityType) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	inner class Model(val activityType: CardioActivityType, var isSelected: Boolean = false)
	private var models: List<Model>
	private var currentActivityType: CardioActivityType

	init {
		currentActivityType = program.activities.keys.first()
		models = program.activities.keys.map { Model(it, it == currentActivityType) }
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_detail_cardio_type_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		return models.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		(holder as ViewHolder).setModel(models[position])
	}

	inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: Model
		private lateinit var activityType: CardioActivityType
		fun setModel(model: Model) {
			this.model = model
			this.activityType = model.activityType
			init()
		}

		private fun init() {
			with (itemView) {
				setOnClickListener {
					currentActivityType = activityType
					models.forEach {
						it.isSelected = (it.activityType == currentActivityType)
					}
					notifyDataSetChanged()
					onChoose.invoke(activityType)
				}
			}
			update()
		}

		private fun update() {
			with (itemView) {
				iconView.setImageResource(activityType.iconResId())
				if (model.isSelected) {
					container.setBackgroundResource(R.drawable.selected_blue_border_rounded_background)
				} else {
					container.setBackgroundResource(R.color.transparent)
				}
			}
		}
	}
}