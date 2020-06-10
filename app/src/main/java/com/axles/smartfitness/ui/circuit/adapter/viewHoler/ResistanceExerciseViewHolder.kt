package com.axles.smartfitness.ui.circuit.adapter.viewHoler

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.program.ProgramType
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.engine.resistance.exercise.ExerciseCategory
import com.axles.smartfitness.engine.resistance.set.DefaultSet
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundItemViewModel
import com.axles.smartfitness.ui.circuit.adapter.CircuitRoundListAdapter
import com.axles.smartfitness.ui.program.dialog.KgEditDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.circuit_exercise_list_item_view.view.*

class ResistanceExerciseViewHolder(
	view: View,
	val fragmentManager: FragmentManager,
	val adapter: CircuitRoundListAdapter
): CircuitRoundItemListViewHolder(view) {
	private lateinit var exercise: ResistanceExercise
	override fun bind(model: CircuitRoundItemViewModel) {
		exercise = model.item.resistanceExercise
		super.bind(model)
	}

	override fun init() {
		with(itemView) {
			setOnClickListener { addKgContainer.makeGone() }
			dragBtn.setOnTouchListener { v, event ->
				when (event.action) {
					MotionEvent.ACTION_DOWN -> { adapter.requestDrag(this@ResistanceExerciseViewHolder) }
					MotionEvent.ACTION_UP -> { v.performClick() }
					else -> {}
				}
				true
			}
			deleteBtn.setOnClickListener { adapter.onDeleteRoundItem(model) }
			photoView.setOnClickListener {
				if (exercise.isOwnExercise) { return@setOnClickListener }
				adapter.onExerciseDetail.invoke(model.item.resistanceExercise.exercise)
			}

			repPickerContainer.setOnClickListener { adapter.onEdit(model) }
			kgPickerContainer.setOnClickListener { onEditKg() }
			kgTitleContainer.setOnClickListener { onSecondaryKg() }
			addKgBtn.setOnClickListener { onAddSecondaryKg() }
		}

		super.init()
	}

	override fun update() {
		with(itemView) {
			titleView.text = exercise.title

			if (exercise.isOwnExercise) {}
			else {
				val defaultSet = exercise.sets[0] as DefaultSet
				when (exercise.category) {
					ExerciseCategory.BODY_WEIGHT -> {
						kgContainer.makeGone()
						repPickerValueView.text = defaultSet.rep
					}
					else -> {
						repPickerValueView.text = defaultSet.rep
						kgPickerValueView.setText(kgString(), TextView.BufferType.SPANNABLE)

						if (exercise.kgIndex() != 0) {
							kgTitleView.text = "${Helper.string(R.string.Kg).toUpperCase()} ${exercise.kgIndex()+1}"
						} else {
							kgTitleView.text = Helper.string(R.string.Kg)
						}
					}
				}
			}

			if (exercise.isOwnExercise || exercise.mainImageUrl.isBlank()) {
				Glide.with(itemView.context).load(R.mipmap.ic_launcher).into(photoView)
			} else {
				Glide.with(itemView.context).load(exercise.mainImageUrl).into(photoView)
			}

			if (adapter.isEditable) {
				editControlContainer.makeVisible()
				repPickerIconView.makeVisible()
				kgPickerIconView.makeVisible()

				kgExpandBtn.makeGone()
			} else {
				editControlContainer.makeGone()
				repPickerIconView.makeGone()
				kgPickerIconView.makeGone()

				kgExpandBtn.makeVisible()
			}
		}
	}

	override fun applyDragging(isDragging: Boolean) {
		with (itemView) {
			addKgContainer.makeGone()
			cardHolder.cardElevation = if (isDragging) 4.dpToPx().toFloat() else 0f
		}
	}

	private fun kgString(): SpannableStringBuilder {
		val kgText = exercise.sets[0].kgToString()
		val spannableStringBuilder = SpannableStringBuilder(kgText)
		if (exercise.kgIndex() == 0) { return spannableStringBuilder }
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

	private fun showAddKgContainer(visible: Boolean) {
		with (itemView) {
			addKgContainer.makeVisibleOrGone(visible)
		}
	}

	private fun onEditKg() {
		if (adapter.isEditable) {
			adapter.onEdit(model)
			return
		}

		KgEditDialog(
			ProgramType.CIRCUIT, exercise,
			didSave = {
				update()
			}
		).show(fragmentManager, "CircuitTrainingExerciseViewHolder.KgEditDialog")
	}

	private fun onSecondaryKg() {
		if (adapter.isEditable) { return }
		with (itemView) {
			if (addKgContainer.isVisible()) addKgContainer.makeGone()
			else {
				addKgContainer.makeVisible()
				kgsRecyclerView.adapter = KgListAdapter(
					exercise,
					onSelect = { index ->
						exercise.setKgIndex(index)
						addKgContainer.makeGone()
						update()
					}
				)
			}
		}
	}

	private fun onAddSecondaryKg() {
		exercise.addKgValue()
		with (itemView) {
			(kgsRecyclerView.adapter as KgListAdapter).add()
			addKgContainer.makeGone()
		}
		update()
	}

	class KgListAdapter(
		val exercise: ResistanceExercise,
		val onSelect: (Int) -> Unit
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		private val values: MutableList<String>

		init {
			val kgCount = exercise.kgCount()
			values = if (kgCount > 1) (0 until kgCount).toList().map { "KG${it+1}" }.toMutableList() else mutableListOf()
		}

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return KgItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.kg_edit_list_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return values.size
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is KgItemViewHolder) { holder.setValue(values[position]) }
		}

		fun add() {
			values.add("KG${values.size+1}")
			notifyDataSetChanged()
		}

		inner class KgItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private lateinit var value: String
			fun setValue(value: String) {
				this.value = value
				init()
			}

			private fun init() {
				with (itemView) {
					setOnClickListener {
						onSelect.invoke(absoluteAdapterPosition)
					}
				}
				update()
			}

			private fun update() {
				with (itemView) {
					titleView.text = value
				}
			}
		}
	}
}