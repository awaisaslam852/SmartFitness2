package com.axles.smartfitness.ui.cardio.subadapter

import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.engine.cardio.CardioActivity
import com.axles.smartfitness.engine.cardio.CardioActivityType
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.ui.cardio.adapter_behaviour.DragAndDropBehaviour

abstract class ActivityListAdapter(
	val program: CardioProgram,
	val activityType: CardioActivityType,
	val fragmentManager: FragmentManager,
	val onEdit: (CardioActivity) -> Unit,
	val didUpdate: () -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	abstract fun update()
}