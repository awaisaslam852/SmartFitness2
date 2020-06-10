package com.axles.smartfitness.ui.resistance.split

import android.content.ClipData
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.resistance_training.split.SplitMuscleGroupModel
import kotlinx.android.synthetic.main.split_muscle_group_list_item.view.*

class SplitMuscleGroupAdapter(
	val shouldEmpty: Boolean = true,
	var onEmpty : ((Boolean)->Unit)? = null,
	var onAdded : ((MuscleGroup)->Unit)? = null
): RecyclerView.Adapter<SplitMuscleGroupAdapter.SplitMuscleGroupViewHolder>() {
	private var models: MutableList<SplitMuscleGroupModel>
	init {
		if (shouldEmpty) { models = mutableListOf() }
		else { models = MuscleGroup.values().map { SplitMuscleGroupModel(it) }.toMutableList() }
	}

	fun isEmpty(): Boolean {
		return allItems().size <= 0
	}

	fun allItems(): MutableList<MuscleGroup> {
		return models.map { it.muscleGroup }.toMutableList()
	}

	fun lockItem(muscleGroup: MuscleGroup) {
		val model = models.firstOrNull { it.muscleGroup == muscleGroup } ?: return
		model.isDragging = true
		notifyItemChanged(models.indexOf(model))
	}

	fun unlockItem(muscleGroup: MuscleGroup) {
		val model = models.firstOrNull { it.muscleGroup == muscleGroup } ?: return
		model.isDragging = false
		notifyItemChanged(models.indexOf(model))
	}

	fun addItem(muscleGroup: MuscleGroup){
		if (hasItem(muscleGroup)) { return }
		models.add(SplitMuscleGroupModel(muscleGroup))
		update()
		onAdded?.invoke(muscleGroup)
	}

	fun removeItem(muscleGroup: MuscleGroup) {
		val model = models.firstOrNull { it.muscleGroup == muscleGroup } ?: return
		notifyItemRemoved(models.indexOf(model))
		models.remove(model)
	}

	private fun hasItem(muscleGroup: MuscleGroup): Boolean {
		return models.any { it.muscleGroup == muscleGroup }
	}

	private fun update() {
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SplitMuscleGroupViewHolder {
		return SplitMuscleGroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.split_muscle_group_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		onEmpty?.invoke(models.isNullOrEmpty())
		return models.size
	}

	override fun onBindViewHolder(holder: SplitMuscleGroupViewHolder, position: Int) {
		holder.setModel(models[position])
	}

	inner class SplitMuscleGroupViewHolder(view: View): RecyclerView.ViewHolder(view){
		private lateinit var model: SplitMuscleGroupModel
		fun setModel(model: SplitMuscleGroupModel){
			this.model = model
			init()
		}

		private fun init() {
			with(itemView) {
				layoutSplitMuscleGroup.setOnLongClickListener {v ->
					val data = ClipData.newPlainText("","")
					val shadow = View.DragShadowBuilder(v)
					val localState = model
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						val result = v.startDragAndDrop(data, shadow, localState, 0)
						result
					} else {
						val result = v.startDrag(data, shadow, localState, 0)
						result
					}
				}
			}

			update()
		}

		private fun update() {
			with(itemView) {
				container.alpha = if (model.isDragging) 0.5f else 1f
				logD("update $absoluteAdapterPosition ${model.isDragging}")
				textViewSplitMuscleGroupName.text = model.muscleGroup.title()
				imageViewSplitMuscleGroup.setImageResource(model.muscleGroup.iconResource())
			}
		}
	}
}