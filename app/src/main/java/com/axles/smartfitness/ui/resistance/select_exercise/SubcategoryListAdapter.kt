package com.axles.smartfitness.ui.resistance.select_exercise

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.exercise.ExerciseManager
import com.axles.smartfitness.engine.resistance.exercise.ExerciseSubCategory
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.resistance_training.exercise.SubcategoryModel
import com.axles.smartfitness.ui.resistance.viewModel.ExerciseListItemViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.subcategory_list_item.view.*

open class SubcategoryListAdapter(
	val muscleGroup: MuscleGroup,
	val category: ExerciseCategory,
	val onExerciseSelected: ((model: ExerciseListItemViewModel, isSelected: Boolean) -> Unit)? = null,
	val onExerciseDetail: ((Exercise) -> Unit)? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	protected var models = listOf<SubcategoryModel>()
	private val selectedExercises = mutableListOf<Exercise>()
	init {
		models = ExerciseManager.subCategories(muscleGroup, category).map { subCategory ->
			SubcategoryModel.create(muscleGroup, category, subCategory)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return SubcategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subcategory_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		if (category == ExerciseCategory.FAVORITE) { return 1 }
		return models.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is SubcategoryViewHolder) { holder.setModel(models[position]) }
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		super.onAttachedToRecyclerView(recyclerView)
		update()
	}

	fun selectedExercises(): List<Exercise> {
		return selectedExercises
	}

	fun update() {
		models = ExerciseManager.subCategories(muscleGroup, category).map { subCategory ->
			SubcategoryModel.create(muscleGroup, category, subCategory)
		}

		if (models.isNotEmpty()) {
			if (category == ExerciseCategory.FAVORITE) {
				models[0].setExercise(ExerciseManager.favorites(muscleGroup))
			}
			val expandedModels = models.filter { it.isExpanded }
			if (expandedModels.isEmpty()) {
				models[0].isExpanded = true
			}
		}

		notifyDataSetChanged()
	}

	inner class SubcategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: SubcategoryModel
		private lateinit var subCategory: ExerciseSubCategory

		fun setModel(model: SubcategoryModel) {
			this.model = model
			this.subCategory = model.subCategory
			init()
		}

		private fun init() {
			if (category == ExerciseCategory.FAVORITE) { model.isExpanded = true }

			with (itemView) {
				container.setOnClickListener {
					if (category == ExerciseCategory.FAVORITE) {
						return@setOnClickListener
					}

					models[absoluteAdapterPosition].isExpanded = !models[absoluteAdapterPosition].isExpanded
					for (index in models.indices) {
						models[index].isExpanded = (index == absoluteAdapterPosition)
					}
					notifyDataSetChanged()
				}
			}

			update()
		}

		private fun update() {
			with (itemView) {
				recyclerViewExercises.adapter = ExerciseAdapter(
					muscleGroup,
					model.exerciseListModels,
					onUpdate = {
						if (category == ExerciseCategory.FAVORITE) {
							model.setExercise(ExerciseManager.favorites(muscleGroup))
							notifyDataSetChanged()
						}
					},
					onDetail = { onExerciseDetail?.invoke(it) },
					onSelected = { model, isSelected ->
						if (isSelected) { selectedExercises.add(model.exercise) }
						else { selectedExercises.remove(model.exercise) }
						onExerciseSelected?.invoke(model, isSelected)
					}
				)

				if (category == ExerciseCategory.FAVORITE) {
					titleView.text = category.title()
					iconView.setImageResource(category.iconResource())

					expandBtn.makeGone()

					if (model.exerciseListModels.isEmpty()) {
						emptyFavorites.makeVisible()
					} else {
						emptyFavorites.makeGone()
					}
				} else {
					titleView.text = model.subCategory.title
					if (model.subCategory.imageUrl.isNotBlank()){
						Glide.with(context).load(model.subCategory.imageUrl).into(iconView)
					}
				}

				if (model.isExpanded) {
					selectedBackground.makeVisibleOrGone(true)
					header.background = ContextCompat.getDrawable(context, R.color.transparent)
					expandBtn.apply {
						imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
						rotation = 0.0f
					}
					titleView.setTextColor(ContextCompat.getColor(context, R.color.white))
					exerciseListContainer.expand()
				} else {
					selectedBackground.makeVisibleOrGone(false)
					header.background = ContextCompat.getDrawable(context, R.color.white)
					expandBtn.apply {
						imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary))
						rotation = 270.0f
					}
					titleView.setTextColor(ContextCompat.getColor(context, R.color.black))
					exerciseListContainer.collapse()
				}
			}
		}
	}

}