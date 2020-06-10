package com.axles.smartfitness.ui.program

import android.app.DatePickerDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.*
import com.axles.smartfitness.engine.cardio.CardioProgram
import com.axles.smartfitness.engine.circuit.CircuitProgram
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.engine.program.ProgramManager
import com.axles.smartfitness.engine.resistance.ResistanceProgram
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.program_fulfill_fragment.*
import java.util.*

class ProgramFulfillFragment: BaseFragment(R.layout.program_fulfill_fragment) {
	private val args: ProgramFulfillFragmentArgs? by navArgs()
	private lateinit var program: Program

	private var buildingDate = Date()
		set(value) {
			field = value
			buildingDateView.text = buildingDate.toShortString()
			updateDate = DateHelper.nextDay(buildingDate)!!
		}
	private var updateDate = DateHelper.nextDay(Date())!!
		set(value) {
			field = value
			updateDateView.text = updateDate.toShortString()
		}

	override fun resolveArguments() {
		if (args == null) { return }
		program = args!!.program
	}

	override fun init() {
		traineeNameView.setText(program.trainee)
		trainerNameView.setText(program.trainer)
		if (program.hasTitle()) {
			programNameView.setText(program.title())
		}

		okBtn.setOnClickListener { onOk() }
		buildingDateView.setOnClickListener { onBuildingDate() }
		selectBuildingDateBtn.setOnClickListener { onBuildingDate() }
		updateDateView.setOnClickListener { onUpdateDate() }
		selectUpdateDateBtn.setOnClickListener { onUpdateDate() }
	}

	override fun update() {
		buildingDateView.text = buildingDate.toShortString()
		updateDateView.text = updateDate.toShortString()
	}

	private fun onOk() {
		program.trainee = traineeNameView.text.toString()
		program.trainer = trainerNameView.text.toString()
		program.builtAt = buildingDate
		program.updatedAt = updateDate

		program.setTitle(programNameView.text.toString())

		ProgramManager.save(program)

		Helper.showToast(requireActivity(), R.string.program_saved)

		when (program) {
			is ResistanceProgram -> {
				val action = ProgramFulfillFragmentDirections.fromResistanceToProgramDetail(program)
				findNavController().navigate(action)
			}
			is CircuitProgram -> {
				val action = ProgramFulfillFragmentDirections.fromCircuitToProgramDetail(program)
				findNavController().navigate(action)
			}
			is CardioProgram -> {
				val action = ProgramFulfillFragmentDirections.fromCardioToProgramDetail(program)
				findNavController().navigate(action)
			}
		}

	}

	private fun onBuildingDate() {
		val year = DateHelper.year(buildingDate)
		val month = DateHelper.month(buildingDate)
		val day = DateHelper.day(buildingDate)
		DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener{ view, year, month, day ->
			buildingDate = DateHelper.date(year, month, day)
		}, year, month, day).show()
	}

	private fun onUpdateDate() {
		val year = DateHelper.year(updateDate)
		val month = DateHelper.month(updateDate)
		val day = DateHelper.day(updateDate)
		val datePickerDialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener{ view, year, month, day ->
			updateDate = DateHelper.date(year, month, day)
		}, year, month, day)
		datePickerDialog.datePicker.minDate = DateHelper.nextDay(buildingDate)!!.time
		datePickerDialog.show()
	}
}