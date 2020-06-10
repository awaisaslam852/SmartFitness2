package com.axles.smartfitness.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.pxToDp
import kotlinx.android.synthetic.main.picker_popup.view.*
import kotlinx.android.synthetic.main.single_picker_container.view.*
import kotlin.math.max

class MultiplePickerPopupWindow(private val parent: View, private val ranges: List<List<Any>>, private val onPick: (values: List<Any>) -> Unit) {
	private val container: View by lazy {
		val popupViewInflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		popupViewInflater.inflate(R.layout.picker_popup, null)
	}

	private lateinit var window: PopupWindow
	init {
		container.pickerListView.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
		container.pickerListView.adapter = PickerListAdapter(ranges,
			onPick = {
				onPick.invoke(it)
			}
		)
		container.setOnTouchListener { view, event ->
			window.dismiss()
			return@setOnTouchListener true
		}
	}

	fun showPopupMenu() {
		container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//		parent.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
		val parentWidth = parent.measuredWidth
		val width = container.measuredWidth
		val height = container.measuredHeight
		val xOffset = container.x - (width-parentWidth)/2
		window = PopupWindow(container, (width), height, true)
		window.showAsDropDown(parent, xOffset.toInt().pxToDp(), 0)//(-164).pxToDp(), (-100).pxToDp())
	}

	fun restorePickerState(values: List<Any>) {
		(container.pickerListView.adapter as PickerListAdapter).setCurrentValues(values)
		container.invalidate()
	}

	private inner class PickerListAdapter(
		val ranges: List<List<Any>>,
		val onPick: (values: List<Any>) -> Unit
	): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
		inner class ValueModel(var value: Any)
		private lateinit var valueModels: List<ValueModel>

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			return PickerListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_picker_container, parent, false))
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			if (holder is PickerListItemViewHolder) {
				holder.setRange(ranges[position])
				if (::valueModels.isInitialized) { holder.setModel(valueModels[position] ) }
			}
		}

		override fun getItemCount(): Int {
			return ranges.size
		}

		fun setCurrentValues(currentValues: List<Any>) {
			this.valueModels = currentValues.map { ValueModel(it) }
			notifyDataSetChanged()
		}

		private inner class PickerListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private lateinit var range: List<Any>
			private lateinit var model: ValueModel
			fun setRange(range: List<Any>) {
				this.range = range
				if (this.range.size > 0) { this.model = ValueModel(this.range.first() ) }
				init()
			}

			fun setModel(model: ValueModel) {
				this.model = model
				update()
			}

			private fun init() {
				itemView.pickerView.minValue = 0
				itemView.pickerView.maxValue = range.size-1
				itemView.pickerView.displayedValues = range.map { it.toString() }.toTypedArray()
				itemView.pickerView.setDividerThickness(1)
				itemView.pickerView.setDividerDistance(34.dpToPx())

				itemView.pickerView.smoothScroll(true, 20)
				itemView.pickerView.isScrollerEnabled = true

				itemView.pickerView.isFadingEdgeEnabled = true
				itemView.pickerView.setTextColorResource(R.color.black)
				itemView.pickerView.setOnValueChangedListener { _, _, newVal ->
					model.value = range[newVal]
					onPick.invoke(valueModels.map { it.value })
					update()
				}

				update()
			}

			private fun update() {
				if (!::model.isInitialized) { return }
				val index = max(range.indexOf(model.value), 0)
				itemView.pickerView.value = index
			}
		}
	}
}
