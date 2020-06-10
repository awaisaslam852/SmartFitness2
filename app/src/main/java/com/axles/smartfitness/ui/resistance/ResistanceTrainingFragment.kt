package com.axles.smartfitness.ui.resistance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramManager
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import com.axles.smartfitness.ui.resistance.exercise.ExercisesListFragment
import com.axles.smartfitness.ui.resistance.split.SplitResistanceTrainingFragment
import kotlinx.android.synthetic.main.muscle_type_item_view.view.*
import kotlinx.android.synthetic.main.resistance_training_fragment.*

class ResistanceTrainingFragment : BaseFragment(R.layout.resistance_training_fragment) {
	private val args: ResistanceTrainingFragmentArgs by navArgs()

	private lateinit var program: ResistanceProgram

	override fun onFragmentResult(requestCode: Int, bundle: Bundle) {
		when (requestCode) {
			ExercisesListFragment.REQUEST_CODE -> {
				if (bundle.containsKey("program")) {
					program = bundle["program"] as ResistanceProgram
					update()
				}
			}
			SplitResistanceTrainingFragment.REQUEST_CODE -> {
				if (bundle.containsKey("program")) {
					program = bundle["program"] as ResistanceProgram
					update()
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		SmartFitnessApplication.instance.resistanceTrainingFragment = this
	}

	override fun onPause() {
		super.onPause()
		SmartFitnessApplication.instance.resistanceTrainingFragment = null
	}

	override fun resolveArguments() {
		if (args.program == null) {
			program = Program.create(ProgramType.RESISTANCE) as ResistanceProgram
			return
		}

		program = args.program as ResistanceProgram
		program.resetKgIndex()
	}

	override fun init() {
		backBtn.setOnClickListener { onBackPressed() }
		discardBtn.setOnClickListener { onDiscard() }
		saveBtn.setOnClickListener { onSave() }
		splitBtn.setOnClickListener { onSplit() }
		deleteSplitBtn.setOnClickListener { onDeleteSplit() }

		muscleTypeRecyclerView.adapter = MuscleTypeListAdapter(
			onChoose = {
				val action = ResistanceTrainingFragmentDirections.actionResistanceTrainingFragmentToExercisesFragment(program, it)
				navigate(action, ExercisesListFragment.REQUEST_CODE)
			}
		)
	}

	override fun update() {
		(muscleTypeRecyclerView.adapter as MuscleTypeListAdapter).notifyDataSetChanged()
		if (program.isNew()) {
			backBtn.makeVisible()
			discardBtn.makeGone()
			saveBtn.makeGone()
		} else {
			backBtn.makeGone()
			discardBtn.makeVisible()
			saveBtn.makeVisible()

			if (program.isEmptyExercises()) {
				saveBtn.setTextColor(Helper.color(R.color.gray))
				saveBtn.isEnabled = false
			} else {
				saveBtn.setTextColor(Helper.color(R.color.white))
				saveBtn.isEnabled = true
			}
		}

		if (program.hasSplitDays()) {
			splitBtn.makeGone()
			deleteSplitBtn.makeVisible()
		} else {
			splitBtn.makeVisible()
			deleteSplitBtn.makeGone()
		}
	}

	override fun onBackPressed() {
		onDiscard()
	}

	private fun onDiscard() {
		if (ProgramManager.isSavedProgram(program)) {
			val dialog = YesNoDialog(getString(R.string.q_leave_this_page_withtout_saving)) { yes ->
				if (yes) {
					findNavController().navigateUp()
				}
			}
			dialog.show(childFragmentManager, DISCARD_DIALOG)
			return
		}

		if (program.isNew()) {
			findNavController().navigateUp()
			return
		}

		val dialog = YesNoDialog(getString(R.string.are_you_sure_you_want_to_delete_this_program)) { yes ->
			if (yes) {
				findNavController().navigate(R.id.mainFragment)
			}
		}
		dialog.show(childFragmentManager, DISCARD_DIALOG)
	}

	private fun onSave() {
		val action = ResistanceTrainingFragmentDirections.toProgramFulfill(program)
		findNavController().navigate(action)
	}

	private fun onSplit() {
		val action = ResistanceTrainingFragmentDirections.toSplitResistanceTrainingFragment(program)
		navigate(action, SplitResistanceTrainingFragment.REQUEST_CODE)
	}

	private fun onDeleteSplit() {
		val dialog = YesNoDialog(Helper.string(R.string.are_you_sure_you_want_to_delete_split_training),
			yesNoListener = { yes ->
				if (yes) {
					program.deleteSplit()
					update()
				}
			}
		)
		requireActivity().run {
			dialog.show(supportFragmentManager, DELETE_SPLIT_TRAINING_DIALOG)
		}
	}

	companion object {
		const val DISCARD_DIALOG = "ResistanceTrainingFragment.DiscardDialogTag"
		const val DELETE_SPLIT_TRAINING_DIALOG = "ResistanceTrainingFragment.DeleteSplitTraining"
	}

	inner class MuscleTypeListAdapter(
		val onChoose: (MuscleGroup) -> Unit
	): RecyclerView.Adapter<MuscleTypeListAdapter.MuscleTypeViewHolder>() {
		inner class Model(val muscleGroup: MuscleGroup, var isSelected: Boolean = false)
		private val models = MuscleGroup.values().map { Model(it, isSelected = program.hasMuscleType(it)) }

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleTypeViewHolder {
			return MuscleTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.muscle_type_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return models.size
		}

		override fun onBindViewHolder(holder: MuscleTypeViewHolder, position: Int) {
			holder.setViewModel(models[position])
			holder.itemView.setOnClickListener {
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

					val day = program.splitDayByMuscleType(viewModel.muscleGroup)
					if (day != null) {
						if (muscleGroup == MuscleGroup.LEGS) {
							legDayView.makeVisible()
							legDayView.text = day.title()
						} else {
							normalDayView.makeVisible()
							normalDayView.text = day.title()
						}
					} else {
						normalDayView.makeGone()
						legDayView.makeGone()
					}
				}
			}
		}
	}
}