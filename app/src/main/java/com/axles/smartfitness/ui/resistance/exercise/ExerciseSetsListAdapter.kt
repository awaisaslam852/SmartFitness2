package com.axles.smartfitness.ui.resistance.exercise

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.WorkoutMethod
import com.axles.smartfitness.engine.resistance.*
import com.axles.smartfitness.engine.resistance.exercise.Exercise.*
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.set.*
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.resistance.edit.SelectTimeFragment
import com.axles.smartfitness.ui.resistance.viewModel.ResistanceSetModel
import kotlinx.android.synthetic.main.exercise_set_item_view.view.*
import kotlinx.android.synthetic.main.exercise_set_item_view.view.addSetBtn
import kotlinx.android.synthetic.main.exercise_set_item_view.view.reduceSetBtn

class ExerciseSetsListAdapter(
	val exercise: ResistanceExercise,
	val fragmentManager: FragmentManager,
	val isEditable: Boolean = true,
	val onEdit: ((ResistanceExercise) -> Unit)? = null,
	val onEditKg: ((setIndex: Int) -> Unit)? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var models = mutableListOf<ResistanceSetModel>()
	init {
		models = exercise.sets.map { ResistanceSetModel(it) }.toMutableList()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return ExerciseSetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exercise_set_item_view, parent, false))
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ExerciseSetViewHolder) { holder.setModel(models[position]) }
	}

	override fun getItemCount(): Int {
		return models.size
	}

	fun update() {
		models = exercise.sets.map { ResistanceSetModel(it) }.toMutableList()
		notifyDataSetChanged()
	}

	inner class ExerciseSetViewHolder(val view: View): RecyclerView.ViewHolder(view) {
		private lateinit var model: ResistanceSetModel
		private lateinit var set: ResistanceSet
		fun setModel(model: ResistanceSetModel) {
			this.model = model
			this.set = model.set
			init()
		}

		private fun init() {
			with (itemView) {
				setOnClickListener { onEdit?.invoke(exercise) }
				addSetBtn.setOnClickListener { onAddValue() }
				reduceSetBtn.setOnClickListener { onReduceValue() }
				restContainer.setOnClickListener {
					onEditRestDuration()
				}
				if (!isEditable) {
					kgContainer.setOnClickListener { onEditKg?.invoke(absoluteAdapterPosition) }
				}
			}

			update()
		}

		private fun update() {
			resolveRest()

			hideAll()
			with (itemView) {
				when (set.workoutMethod()) {
					WorkoutMethod.DEFAULT -> {
						repsContainer.visibility = View.VISIBLE
						kgContainer.visibility = View.VISIBLE

						setsView.text = "${(set as DefaultSet).set}"
						repsView.text = (set as DefaultSet).rep
						kgsView.setText(kgString(), TextView.BufferType.SPANNABLE)
					}
					WorkoutMethod.DROP_SET -> {
						dropSetContainer.visibility = View.VISIBLE
						kgContainer.visibility = View.VISIBLE
						if (isEditable) {
							addSetBtn.makeVisible()
							reduceSetBtn.makeVisible()
						} else {
							addSetBtn.makeGone()
							reduceSetBtn.makeGone()
						}

						setsView.text = (absoluteAdapterPosition+1).toString()
						dropSetView.text = (set as DropSet).reps.joinToString(separator = "-")
						kgsView.setText(kgString(), TextView.BufferType.SPANNABLE)
					}
					WorkoutMethod.PERCENT -> {
						rmContainer.visibility = View.VISIBLE
						kgContainer.visibility = View.VISIBLE
						percentContainer.visibility = View.VISIBLE

						setsView.text = (absoluteAdapterPosition+1).toString()
						rmView.text = (set as PercentSet).rm
						kgsView.setText(kgString(), TextView.BufferType.SPANNABLE)
						percentView.text = "${(set as PercentSet).percent}%"
					}
					WorkoutMethod.PYRAMID -> {
						repsContainer.visibility = View.VISIBLE
						kgContainer.visibility = View.VISIBLE

						setsView.text = (absoluteAdapterPosition+1).toString()
						if (set is PyramidSet) {
							repsView.text = (set as PyramidSet).rep
							kgsView.setText(kgString(), TextView.BufferType.SPANNABLE)
						}
					}
					else -> {
						superSetContainer.visibility = View.VISIBLE
						repsContainer.visibility = View.VISIBLE
						kgContainer.visibility = View.VISIBLE

						setsView.text = (absoluteAdapterPosition+1).toString()
						superSetView.text = (set as SuperSet).exercises.map { "Exercise ${(set as SuperSet).exercises.indexOf(it)+1}" }.joinToString(separator = "\n")
						repsView.text = (set as SuperSet).reps.joinToString(separator = "\n")
						kgsView.setText(kgString(), TextView.BufferType.SPANNABLE)
					}
				}

				if (!exercise.isOwnExercise && exercise.category == ExerciseCategory.BODY_WEIGHT) {
					kgContainer.makeGone()
				}
			}
		}

		private fun kgString(): SpannableStringBuilder {
			val kgText = set.kgToString()
			val spannableStringBuilder = SpannableStringBuilder(kgText)
			var index = kgText.indexOf("?")
			val boldTypeFace = Typeface.create(ResourcesCompat.getFont(itemView.context, R.font.os_bold), Typeface.BOLD)
			while (index >= 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					spannableStringBuilder.setSpan(
						TypefaceSpan(boldTypeFace),
						index, index+1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
					)
				} else {
					spannableStringBuilder.setSpan(
						StyleSpan(Typeface.BOLD),
						index, index+1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
					)
				}
				index = kgText.indexOf("?", index+1)
			}
			return spannableStringBuilder
		}

		private fun resolveRest() {
			with (itemView) {
				if (absoluteAdapterPosition > 0 && absoluteAdapterPosition >= models.lastIndex) {
					restContainer.makeGone()
					return
				}

				restContainer.makeVisible()
				restDurationView.text = set.restTime.toTimerString()
			}
		}

		private fun hideAll() {
			val containers = listOf(itemView.superSetContainer, itemView.repsContainer, itemView.rmContainer, itemView.dropSetContainer, itemView.kgContainer, itemView.percentContainer,
				itemView.addSetBtn, itemView.reduceSetBtn)
			for (container in containers) {
				container.makeGone()
			}
		}

		private fun onAddValue() {
			if (set.valueCount() >= 6) {
				Helper.showErrorToast(itemView.context, "The maximum is 6 drops")
				return
			}

			set.addValue()
			update()
		}

		private fun onReduceValue() {
			if (set.valueCount() <= 2) {
				Helper.showErrorToast(itemView.context, "The minimum is 2 drops")
				return
			}

			set.reduceValue()
			update()
		}

		private fun onEditRestDuration() {
			val dialog = SelectTimeFragment(
				set.restTime,
				onSave = {
					set.restTime = it
					update()
				}
			)
			dialog.show(fragmentManager, "ExercisesListFragment.SelectTimeFragment")
		}
	}
}