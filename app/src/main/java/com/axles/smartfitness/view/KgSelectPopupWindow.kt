package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.resistance.ResistanceExercise
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.pxToDp
import kotlinx.android.synthetic.main.kg_edit_list_item_view.view.*
import kotlinx.android.synthetic.main.kg_selection_popup.view.*

class KgSelectPopupWindow(
	val parent : View,
	val exercise: ResistanceExercise,
	val onSelect: (index: Int, value: String) -> Unit,
	val didAdd: () -> Unit
) {
	private val container : View by lazy {
		val popupViewInflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		popupViewInflater.inflate(R.layout.kg_selection_popup, null)
	}

	private val popupWindow: PopupWindow by lazy {
		PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
	}

	init {
		with (container) {
			addBtn.setOnClickListener {
				exercise.addKgValue()
				popupWindow.dismiss()
				didAdd.invoke()
			}
		}
		update()
	}

	fun show() {
		popupWindow.showAsDropDown(parent, 20.pxToDp(), 0.pxToDp())//(-164).pxToDp(), (-100).pxToDp())
	}

	private fun update() {
		with (container) {
			val kgCount = exercise.kgCount()
			val kgLabels = if (kgCount > 1) (0 until kgCount).toList().map { "KG${it+1}" }.toMutableList() else mutableListOf()
			recyclerView.adapter = ValueListAdapter(kgLabels)
		}
	}

	inner class ValueListAdapter(
		val values: MutableList<String>
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return ValueItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.kg_edit_list_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return values.size
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is ValueItemViewHolder) { holder.setValue(values[position]) }
		}

		fun add() {
			values.add("KG${values.size+1}")
			notifyDataSetChanged()
		}
	}

	inner class ValueItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
		private lateinit var value: String
		fun setValue(value: String) {
			this.value = value
			init()
		}

		private fun init() {
			with (itemView) {
				setOnClickListener {
					this@KgSelectPopupWindow.popupWindow.dismiss()
					onSelect.invoke(absoluteAdapterPosition, value)
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