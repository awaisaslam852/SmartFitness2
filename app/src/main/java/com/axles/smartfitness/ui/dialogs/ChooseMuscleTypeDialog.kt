package com.axles.smartfitness.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.extensions.makeVisibleOrGone
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.choose_muscle_type_dialog.view.*
import kotlinx.android.synthetic.main.choose_muscle_type_dialog_muscle_type_item_view.view.*

class ChooseMuscleTypeDialog(
	val title: String,
	var onChoose: ((MuscleGroup) -> Unit)
): BottomSheetDialogFragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		super.onCreateView(inflater, container, savedInstanceState)
		val rootView: View = inflater.inflate(R.layout.choose_muscle_type_dialog, container, false)

		rootView.cancelBtn.setOnClickListener { dismiss() }
		rootView.titleView.text = title
		rootView.muscleTypeList.adapter = MuscleTypeListAdapter()

		return rootView
	}

	inner class MuscleTypeListAdapter: RecyclerView.Adapter<MuscleTypeListAdapter.MuscleTypeViewHolder>() {
		inner class Model(val muscleGroup: MuscleGroup, var isSelected: Boolean = false)
		private val models = MuscleGroup.values().map { Model(it) }

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleTypeViewHolder {
			return MuscleTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.choose_muscle_type_dialog_muscle_type_item_view, parent, false))
		}

		override fun getItemCount(): Int {
			return models.size
		}

		override fun onBindViewHolder(holder: MuscleTypeViewHolder, position: Int) {
			holder.setViewModel(models[position])
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
				with (itemView) {
					setOnClickListener {
						this@ChooseMuscleTypeDialog.dismiss()
						onChoose.invoke(muscleGroup)
					}
				}
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
				}
			}
		}
	}
}