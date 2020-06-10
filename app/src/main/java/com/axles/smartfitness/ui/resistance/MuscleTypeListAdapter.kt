package com.axles.smartfitness.ui.resistance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.extensions.makeVisibleOrGone
import kotlinx.android.synthetic.main.muscle_type_item_view.view.*

class MuscleTypeListAdapter(
	val onChoose: (MuscleGroup) -> Unit
): RecyclerView.Adapter<MuscleTypeListAdapter.MuscleTypeViewHolder>() {
	inner class Model(val muscleGroup: MuscleGroup, var isSelected: Boolean = false)
	private val models = MuscleGroup.values().map { Model(it) }

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleTypeViewHolder {
		return MuscleTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.muscle_type_item_view, parent, false))
	}

	override fun getItemCount(): Int {
		return models.size
	}

	override fun onBindViewHolder(holder: MuscleTypeViewHolder, position: Int) {
		holder.setViewModel(models[position])
		holder.itemView.setOnClickListener {
			for (index in models.indices) {
				models[index].isSelected = (position == index)
			}
			notifyDataSetChanged()
			onChoose(models[position].muscleGroup)
		}
	}

	inner class MuscleTypeViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var viewModel: Model
		private lateinit var muscleGroup: MuscleGroup
		fun setViewModel(viewModel: Model) {
			this.viewModel = viewModel
			this.muscleGroup = this.viewModel.muscleGroup
			init()
		}

		private fun init() {
			update()

			bindDayLabel()
			bindIsDone()
		}

		private fun update() {
			with(itemView) {
				imageView.setImageResource(muscleGroup.iconResource())
				topBarTitleView.text = muscleGroup.title()

				if (viewModel.isSelected) {
					borderView.makeVisibleOrGone(true)
					topBarTitleView.setTextColor(Helper.color(R.color.white))
					titleContainer.setBackgroundColor(Helper.color(R.color.selected_muscle_type_title_background))
				} else {
					borderView.makeVisibleOrGone(false)
					topBarTitleView.setTextColor(Helper.color(R.color.black))
					titleContainer.setBackgroundColor(Helper.color(R.color.transparent))
				}
			}
		}

		private fun bindIsDone() {
			/*
			if (type.isDone){
				logD("model $type (${type.name} is done")
				itemView.layoutMuscleGroup.background = ContextCompat.getDrawable(itemView.context, R.color.colorPrimary)
				itemView.textViewMuscleGroup.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
			}

			 */
		}

		private fun bindDayLabel() {
			/*
			val dayLabel = itemView.textViewDayLabel

			if (type.day.isBlank()){
				dayLabel.makeInvisible()

			} else {
				dayLabel.makeVisible()
				dayLabel.text = type.day
			}

			if (type.muscleGroup == MuscleType(MuscleType.Type.LEGS)){

				val params = dayLabel.layoutParams as ConstraintLayout.LayoutParams
				params.topMargin = 66.dpToPx()
				dayLabel.layoutParams = params
			}
			 */
		}
	}
}