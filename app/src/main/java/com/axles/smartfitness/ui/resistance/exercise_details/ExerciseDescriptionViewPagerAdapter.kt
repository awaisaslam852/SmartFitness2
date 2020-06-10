package com.axles.smartfitness.ui.resistance.exercise_details

import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import kotlinx.android.synthetic.main.exercise_detail_description_item_view.view.*

class ExerciseDescriptionViewPagerAdapter(
	val exercise: Exercise
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_detail_description_item_view, parent, false))
	}

	override fun getItemCount(): Int {
		return exercise.descriptions.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) {
			holder.setDescription(exercise.descriptions[position])
		}
	}

	inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var description: Exercise.Description
		fun setDescription(description: Exercise.Description) {
			this.description = description
			init()
		}

		private fun init() {
			with (itemView) {

			}
			update()
		}

		private fun update() {
			with (itemView) {
				val title = description.title + ":"
				val spannableStringBuilder = SpannableStringBuilder(title)
				spannableStringBuilder.setSpan(UnderlineSpan(), 0, title.length, 0)
				titleView.text = spannableStringBuilder

				contentView.text = description.content
			}
		}
	}
}