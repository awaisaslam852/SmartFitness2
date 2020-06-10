package com.axles.smartfitness.ui.resistance.split

import android.view.DragEvent
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.engine.core.MuscleGroup
import com.axles.smartfitness.engine.core.SplitDay
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.resistance_training.split.SplitMuscleGroupModel
import com.axles.smartfitness.ui.base.BaseFragment
import com.phelat.navigationresult.navigateUp
import kotlinx.android.synthetic.main.split_resistance_training_fragment.*
import kotlinx.android.synthetic.main.toolbar_title_cancel.*

class SplitResistanceTrainingFragment : BaseFragment(R.layout.split_resistance_training_fragment), View.OnDragListener {
	companion object {
		const val REQUEST_CODE = 0x05200334
	}

	private val args: SplitResistanceTrainingFragmentArgs by navArgs()
	private val adapters = mutableListOf<SplitMuscleGroupAdapter>()
	private lateinit var program: ResistanceProgram

	override fun resolveArguments() {
		program = args!!.program as ResistanceProgram
	}

	override fun init() {
		buttonFinish.setOnClickListener { onFinish() }

		setupMusclesRecycler()
		setupDays()
		setupCancel()
	}

	override fun update() {

	}

	private fun setupCancel() {
		cancelBtn.setOnClickListener {
			findNavController().navigateUp()
		}
	}

	private fun setupDays() {

		val dayText = getString(R.string.day)

		val adapter1 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere1.makeInvisible()
				} else {
					textViewDragHere1.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		aRecyclerView.adapter = adapter1
		aRecyclerView.setOnDragListener(this)
		aContainer.setOnDragListener(this)
		aTitleView.text = "A"
		val day1 = makeVerticalText("$dayText 1")
		aVerticalTitleView.text = day1
		adapters.add(adapter1)

		val adapter2 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere2.makeInvisible()
				} else {
					textViewDragHere2.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		bRecyclerView.adapter = adapter2
		bRecyclerView.setOnDragListener(this)
		bContainer.setOnDragListener(this)
		bTitleView.text = "B"
		val day2 = makeVerticalText("$dayText 2")
		bVerticalTitleView.text = day2
		adapters.add(adapter2)

		val adapter3 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere3.makeInvisible()
				} else {
					textViewDragHere3.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		cRecyclerView.adapter = adapter3
		cRecyclerView.setOnDragListener(this)
		cContainer.setOnDragListener(this)
		cTitleView.text = "C"
		val day3 = makeVerticalText("$dayText 3")
		cVerticalTitleView.text = day3

		adapters.add(adapter3)


		val adapter4 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere4.makeInvisible()
				} else {
					textViewDragHere4.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		dRecyclerView.adapter = adapter4
		dRecyclerView.setOnDragListener(this)
		dContainer.setOnDragListener(this)
		dTitleView.text = "D"
		val day4 = makeVerticalText("$dayText 4")
		dVerticalTitleView.text = day4
		adapters.add(adapter4)

		val adapter5 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere5.makeInvisible()
				} else {
					textViewDragHere5.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		eRecyclerView.adapter = adapter5
		eRecyclerView.setOnDragListener(this)
		eContainer.setOnDragListener(this)
		eTitleView.text = "E"
		val day5 = makeVerticalText("$dayText 5")
		eVerticalTitleView.text = day5
		adapters.add(adapter5)


		val adapter6 = SplitMuscleGroupAdapter().apply {
			onEmpty = { isEmpty ->
				if (!isEmpty) {
					textViewDragHere6.makeInvisible()
				} else {
					textViewDragHere6.makeVisible()
				}
			}

			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		fRecyclerView.adapter = adapter6
		fRecyclerView.setOnDragListener(this)
		fContainer.setOnDragListener(this)
		fTitleView.text = "F"
		val day6 = makeVerticalText("$dayText 6")
		fVerticalTitleView.text = day6
		adapters.add(adapter6)

	}

	private fun makeVerticalText(text: String): String {
		return text.map {
			"\n$it"
		}.joinToString("")
	}

	private fun setupMusclesRecycler() {
		val splitMuscleGroupAdapter = SplitMuscleGroupAdapter(shouldEmpty = false)
		splitMuscleGroupAdapter.apply {
			onEmpty = {
				if (it)
					logD("initial muscle group is empty")
			}
			onAdded = { model->
				adapters.forEach { adapter->
					if (adapter != this)
						adapter.removeItem(model).also { logD("try to remove $model") }
				}
			}
		}
		muscleTypeRecyclerView.adapter = splitMuscleGroupAdapter
		muscleTypeRecyclerView.setOnDragListener(this)
		adapters.add(splitMuscleGroupAdapter)

	}

	private fun onFinish() {
		val days = mutableMapOf<SplitDay, MutableList<MuscleGroup>>()
		for (index in 1 until adapters.size) {
			if (adapters[index].isEmpty()) { continue }
			val day = SplitDay.create(index-1)
			days[day] = adapters[index].allItems()
		}

		program.split(days)
		navigateUp(REQUEST_CODE, bundleOf("program" to program))
	}

	override fun onDrag(view: View?, event: DragEvent?): Boolean {
		if (view == null || event == null) {
			return true
		}

		val sourceModel = event.localState as SplitMuscleGroupModel
		val sourceMuscleType = sourceModel.muscleGroup

		var targetRecyclerView: RecyclerView? = null
		if (view.id == R.id.aContainer) {	targetRecyclerView = aRecyclerView }
		if (view.id == R.id.bContainer) { targetRecyclerView = bRecyclerView }
		if (view.id == R.id.cContainer) { targetRecyclerView = cRecyclerView }
		if (view.id == R.id.dContainer) { targetRecyclerView = dRecyclerView }
		if (view.id == R.id.eContainer) { targetRecyclerView = eRecyclerView }
		if (view.id == R.id.fContainer) { targetRecyclerView = fRecyclerView }

		when (event.action) {
			DragEvent.ACTION_DRAG_STARTED -> {
				(muscleTypeRecyclerView.adapter as SplitMuscleGroupAdapter).lockItem(sourceMuscleType)
			}
			DragEvent.ACTION_DRAG_LOCATION -> {
			}
			DragEvent.ACTION_DROP -> {
				logD("drop at ${view.id}, info ${sourceModel.muscleGroup.name}")
				if (view is RecyclerView) {
					logD("onDragView is recycler")
					val adapter = view.adapter
					if (adapter is SplitMuscleGroupAdapter) {
						adapter.addItem(sourceMuscleType)
					}
				}

				if (targetRecyclerView != null) {
					(targetRecyclerView.adapter as SplitMuscleGroupAdapter).addItem(sourceMuscleType)
				}
			}
			DragEvent.ACTION_DRAG_ENDED -> {
				(muscleTypeRecyclerView.adapter as SplitMuscleGroupAdapter).unlockItem(sourceMuscleType)
			}
		}
		return true
	}
}