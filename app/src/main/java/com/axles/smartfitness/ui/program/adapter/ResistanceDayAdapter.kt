package com.axles.smartfitness.ui.program.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import kotlinx.android.synthetic.main.circuit_jumping_rope_activity_list_item.view.titleView
import kotlinx.android.synthetic.main.program_view_resistance_day_list_item.view.*

class ResistanceDayAdapter(
	val program: ResistanceProgram,
	val onSelectDay: (SplitDay?) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var splitDays: List<SplitDay> = program.splitDays.keys.toList()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_view_resistance_day_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		if (program.hasExercises(null)) {
			return splitDays.size + 1
		} else {
			return splitDays.size
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) {
			if (position < splitDays.size) { holder.setDay(splitDays[position]) }
			else { holder.setOther() }
		}
	}

	inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var splitDay: SplitDay
		fun setDay(splitDay: SplitDay) {
			this.splitDay = splitDay
			init()
		}

		fun setOther() {
			with(itemView) {
				titleView.text = ""
				otherIndicatorView.makeVisible()

				setOnClickListener { onSelectDay.invoke(null) }
			}
		}

		private fun init() {
			with(itemView) {
				setOnClickListener { onSelectDay.invoke(splitDay) }
			}

			update()
		}

		private fun update() {
			with (itemView) {
				otherIndicatorView.makeGone()
				titleView.text = splitDay.title()
				dayIndexView.text = "DAY ${splitDays.indexOf(splitDay)+1}"
			}
		}
	}
}