package com.axles.smartfitness.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.cardio_training.CardioActivityModel
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.ui.cardio.CardioActivityViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.choose_cardio_activity_type_dialog.*
import kotlinx.android.synthetic.main.choose_cardio_activity_item_view.view.*

class ChooseCardioActivityTypeDialog(
	val onChoose: (CardioActivityType) -> Unit
): BottomSheetDialogFragment() {
	lateinit var viewModel: CardioActivityViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.choose_cardio_activity_type_dialog, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(requireActivity())[CardioActivityViewModel::class.java]
		initView()
	}

	private fun initView() {
		cancelBtn.setOnClickListener { dismiss() }
		activityTypesRecyclerView.apply {
			layoutManager = GridLayoutManager(requireContext(), 2)
			adapter = CircuitCardioAdapter()
		}
	}

	inner class CircuitCardioAdapter: RecyclerView.Adapter<CircuitCardioAdapter.ViewHolder>() {
		val items = listOf(RUNNING, CYCLING, ELLIPTICAL, ROWING_MACHINE, STAIR_CLIMBING, JUMPING_ROPE).map { CardioActivityModel(it) }
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
			return ViewHolder(View.inflate(parent.context, R.layout.choose_cardio_activity_item_view, null))
		}

		override fun getItemCount() = items.count()

		override fun onBindViewHolder(holder: ViewHolder, position: Int) {
			holder.bind(items[position])
		}

		inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
			fun bind(item: CardioActivityModel) {
				Glide.with(itemView.context)
					.load(item.activityType.iconResId())
					.optionalFitCenter()
					.override(Target.SIZE_ORIGINAL)
					.into(itemView.iconView)
				itemView.titleView.text = item.activityType.title()
				itemView.setOnClickListener {
					onChoose.invoke(item.activityType)
					dismiss()
				}
			}
		}
	}
}