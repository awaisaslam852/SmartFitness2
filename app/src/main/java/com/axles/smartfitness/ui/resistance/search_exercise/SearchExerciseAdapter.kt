package com.axles.smartfitness.ui.resistance.search_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import kotlinx.android.synthetic.main.exercise_search_list_item.view.*

class SearchExerciseAdapter (private val onExerciseClicked: (Exercise)->Unit)
	: RecyclerView.Adapter<SearchExerciseAdapter.SearchViewHolder>(){

	private val exercises = mutableListOf<Exercise>()

	fun setExercises(newModel: List<Exercise>){
		exercises.clear()
		exercises.addAll(newModel)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
			= SearchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_search_list_item, parent, false))

	override fun getItemCount() = exercises.size

	override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
		holder.bind(exercises[position])
	}

	inner class SearchViewHolder(view: View): RecyclerView.ViewHolder(view){
		fun bind(exercise: Exercise){
			with(itemView){
				textViewSearchItem.text = exercise.title
				layoutSearchExercise.setOnClickListener {
					onExerciseClicked.invoke(exercises[adapterPosition])
				}
			}
		}
	}
}