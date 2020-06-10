package com.axles.smartfitness.ui.resistance.select_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.resistance_training.exercise.ExerciseCategoryViewModel
import kotlinx.android.synthetic.main.exercise_category_list_item.view.*

class ExerciseCategoryListAdapter(
	val onSelect : (ExerciseCategory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var items = mutableListOf<ExerciseCategoryViewModel>()

	init {
		items = ExerciseCategory.values().map { ExerciseCategoryViewModel(it) }.toMutableList()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ExerciseCategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_category_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		return items.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ExerciseCategoryViewHolder) {
			holder.setModel(items[position])
			with (holder.itemView) {
				setOnClickListener {
					for (index in items.indices) {
						items[index].isActive = (index == position)
					}
					onSelect.invoke(items[position].category)
					notifyDataSetChanged()
				}
			}
		}
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		super.onAttachedToRecyclerView(recyclerView)
		if (items.isNotEmpty()) {
			if (!items.any { it.isActive }) {
				items[0].isActive = true
				onSelect.invoke(items[0].category)
			} else {
				val selectedItem = items.first { it.isActive }
				onSelect.invoke(selectedItem.category)
			}
		}
	}

	inner class ExerciseCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: ExerciseCategoryViewModel
		private lateinit var category: ExerciseCategory
		fun setModel(viewModel: ExerciseCategoryViewModel){
			this.model = viewModel
			this.category = viewModel.category
			init()
		}

		private fun init() {
			with (itemView) {
			}
			update()
		}

		private fun update() {
			with (itemView) {
				topBarTitleView.text = category.title()
				iconView.setImageResource(category.iconResource())

				if (model.isActive){
					container.background = itemView.context.getDrawable(R.drawable.blue_thin_stroke_background)
				} else {
					container.background = itemView.context.getDrawable(R.drawable.muscle_type_image_background)
				}
			}
		}
	}

}