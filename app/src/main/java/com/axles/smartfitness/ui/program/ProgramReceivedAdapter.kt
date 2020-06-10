package com.axles.smartfitness.ui.program

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.program.ProgramHomeModel
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.program_received_list_item.view.*

class ProgramReceivedAdapter(
	private val fragmentManager: FragmentManager,
	private val isEmptyListener: (Boolean)->Unit,
	val didAccept: () -> Unit,
	val didReject: () -> Unit
): RecyclerView.Adapter<ProgramReceivedAdapter.ReceivedProgramViewHolder>() {

	private val model: MutableList<ProgramHomeModel<Int>> = mutableListOf(
		ProgramHomeModel<Int>("Circuit training", "March 9, 2019").apply {
			image = R.drawable.placeholder_2
		},
		ProgramHomeModel<Int>("2 days program", "May 10, 2016").apply {
			image = R.drawable.placeholder_2
		})

	init {
		callIsEmptyListener()
	}

	private fun callIsEmptyListener(){
		isEmptyListener.invoke(model.isEmpty())
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedProgramViewHolder {
		return ReceivedProgramViewHolder(
			LayoutInflater.from(parent.context)
				.inflate(R.layout.program_received_list_item, parent, false),
			fragmentManager
		)
	}

	override fun getItemCount() = model.size

	override fun onBindViewHolder(holder: ReceivedProgramViewHolder, position: Int) {
		holder.bindProgram(model[position], position)
		holder.onDeleteListener = {
			model.removeAt(it)
			callIsEmptyListener()
			this.notifyDataSetChanged()
		}
	}

	inner class ReceivedProgramViewHolder(view: View, val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(view) {

		var onDeleteListener: ((position: Int) -> Unit)? = null

		fun bindProgram(program: ProgramHomeModel<Int>, position: Int) {
//            itemView.textViewProgramName.text = program.title
			val programTitleLabelAndText = "${itemView.context.getString(R.string.program_name_colon)} ${program.title}"
			itemView.nameView.text = programTitleLabelAndText
			itemView.receivedOnView.text = program.date
			itemView.buttonDelete.setOnClickListener {
				YesNoDialog(it.resources.getString(R.string.are_you_sure_you_want_to_delete_this_program)){yes->
					if (yes){
						onDeleteListener?.invoke(position)
					}
				}.show(fragmentManager, "ProgramReceivedAdapter.Delete.YesNoDialog")
			}
			Glide.with(itemView.context).load(program.image).into(itemView.photoView)

			with (itemView) {
				acceptBtn.setOnClickListener {
					model.remove(model.last())
					notifyDataSetChanged()
					didAccept.invoke()
					callIsEmptyListener()
				}

				rejectBtn.setOnClickListener {
					YesNoDialog(it.resources.getString(R.string.q_stop_receiving_programs_from_unknown)){yes->
						if (yes){
							model.remove(model.last())
							notifyDataSetChanged()
							didReject.invoke()
							callIsEmptyListener()
						}
					}.show(fragmentManager, "ProgramReceivedAdapter.Delete.YesNoDialog")
				}

				closeBtn.setOnClickListener {
					YesNoDialog(it.resources.getString(R.string.are_you_sure_you_want_to_delete_this_program)){yes->
						if (yes){
							model.remove(model.last())
							notifyDataSetChanged()
							didReject.invoke()
							callIsEmptyListener()
						}
					}.show(fragmentManager, "ProgramReceivedAdapter.Delete.YesNoDialog")
				}
			}
		}
	}
}