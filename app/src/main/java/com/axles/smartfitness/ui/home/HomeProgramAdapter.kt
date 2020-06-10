package com.axles.smartfitness.ui.programs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.toLongString
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.home.ProgramMenuPopUpWindow
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.home_program_list_item.view.*

class HomeProgramAdapter(
	private val fragmentManager: FragmentManager,
	val onSelect: (Program) -> Unit,
	var onEdit: (Program) -> Unit,
	val onShare: (Program) -> Unit,
	val onDelete: (Program) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	inner class Model(val program: Program)
	private lateinit var models: List<Model>

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return HomeProgramViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_program_list_item, parent, false))
	}

	override fun getItemCount(): Int {
		if (::models.isInitialized) { return models.size }
		return 0
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is HomeProgramViewHolder) { holder.setModel(models[position]) }
	}

	fun update(programs: List<Program>) {
		models = programs.map { Model(it) }
		notifyDataSetChanged()
	}

	inner class HomeProgramViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: Model
		private lateinit var program: Program
		fun setModel(model: Model) {
			this.model = model
			this.program = model.program
			init()
		}

		private fun init() {
			with (itemView) {
				imageViewProgram.setOnClickListener { onSelect.invoke(program) }
				imageButtonMore.setOnClickListener {
					val popup = ProgramMenuPopUpWindow(itemView.container,
						onEdit = { this@HomeProgramAdapter.onEdit.invoke(program) },
						onShare = { this@HomeProgramAdapter.onShare.invoke(program) },
						onDelete = {
							YesNoDialog(Helper.string(R.string.are_you_sure_you_want_to_delete_this_program)){ yes ->
								if (yes){
									this@HomeProgramAdapter.onDelete.invoke(program)
								}
							}.show(fragmentManager, "HomeProgram.Dialog")
						},
						onDismiss = { grayView.makeGone() }
					)
					popup.showPopupMenu()
					grayView.makeVisible()
				}
				grayView.setOnClickListener { grayView.makeGone() }
			}
			update()
		}

		private fun update() {
			itemView.textViewProgramTitle.text = program.title()
			itemView.textViewDate.text = program.builtAt.toLongString()

			Glide.with(itemView.context)
				.load(program.imageUrl)
				.into(itemView.imageViewProgram)
		}
	}
}