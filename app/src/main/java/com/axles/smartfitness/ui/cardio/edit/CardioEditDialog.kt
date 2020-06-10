package com.axles.smartfitness.ui.cardio.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.cardio.CardioActivity.*
import com.axles.smartfitness.engine.cardio.*
import com.axles.smartfitness.engine.cardio.CardioActivityType.*
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.view.OnePickerPopupWindow
import com.axles.smartfitness.view.PickerValueFactory
import com.axles.smartfitness.view.TimeDistancePopupWindow
import com.axles.smartfitness.view.TwoPickersPopupWindow
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.edit_cardio_row_fragment.*
import kotlinx.android.synthetic.main.time_picker_layout.view.*
import java.lang.NumberFormatException
import java.util.*

class CardioEditDialog(
	val cardioActivity: CardioActivity,
	val didEdit: (CardioActivity) -> Unit
): BottomSheetDialogFragment() {
	private var activityType : CardioActivityType
	private var valueType : ValueType = ValueType.TIME

	init {
		activityType = cardioActivity.type
		when (cardioActivity) {
			is RunningActivity -> activityType = RUNNING
			is CyclingActivity -> activityType = CYCLING
			is EllipticalActivity -> activityType = ELLIPTICAL
			is RowingMachineActivity -> activityType = ROWING_MACHINE
			is StairClimbingActivity -> activityType = STAIR_CLIMBING
			is SwimmingActivity -> activityType = SWIMMING
			is JumpingRopeActivity -> activityType = JUMPING_ROPE
		}
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.edit_cardio_row_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		resolveCardioActivityLayout()
	}

	private fun resolveCardioActivityLayout() {
		cancelBtn.setOnClickListener {
			dismiss()
		}

		when (cardioActivity){
			is RunningActivity -> resolveRunningLayout()
			is CyclingActivity -> resolveCyclingLayout()
			is EllipticalActivity -> resolveEllipticalLayout()
			is RowingMachineActivity -> resolveRowingMachineLayout()
			is StairClimbingActivity -> resolveStairClimbingLayout()
			is SwimmingActivity -> resolveSwimmingLayout()
			is JumpingRopeActivity -> resolveJumpingRopeLayout()
		}
	}

	private fun resolveSwimmingLayout() {
		val activity = cardioActivity as SwimmingActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createAlternativeFirstDistanceValues(),
			".",
			PickerValueFactory.createAlternativeSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}
			timeDistancePopup.showPopUp()
		}

		val stylePopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createStyle(requireContext())
		) {
			activity.style = SwimmingActivity.Style.fromString(requireContext(), it.toString())
			layoutPickerCardioAttribute1.valueView.text = it.toString()
		}

		textViewEditCardioAttribute1.text = getString(R.string.style_colon)
		val styleText = getString(activity.style.styleRes)
		layoutPickerCardioAttribute1.valueView.text = styleText
		stylePopup.restorePickerState(styleText)
		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			stylePopup.showPopupMenu()
		}

		val exercisePopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createExercise(requireContext())
		) {
			activity.exercise = SwimmingActivity.ExerciseType.fromString(requireContext(), it.toString())
			layoutPickerCardioAttribute2.valueView.text = it.toString()
		}

		val exerciseText = getString(R.string.exercise_colon)
		textViewEditCardioAttribute2.text = exerciseText
		exercisePopup.restorePickerState(exerciseText)
		layoutPickerCardioAttribute2.valueView.text = getString(activity.exercise.swimmingExerciseRes)
		layoutPickerCardioAttribute2.setOnClickListener {
			exercisePopup.showPopupMenu()
		}

		val intensityPopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute3,
			PickerValueFactory.createSwimmingIntensity(requireContext())
		) {
			activity.intensity = SwimmingActivity.Intensity.fromString(requireContext(), it.toString())
			layoutPickerCardioAttribute3.valueView.text = it.toString()
		}
		textViewEditCardioAttribute3.text = getString(R.string.intensity_colon)
		val intensityText = getString(activity.intensity.swimmingIntensityRes)
		layoutPickerCardioAttribute3.valueView.text = intensityText
		intensityPopup.restorePickerState(intensityText)

		layoutPickerCardioAttribute3.setOnClickListener {
			intensityPopup.showPopupMenu()
		}

		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveStairClimbingLayout() {
		val activity = cardioActivity as StairClimbingActivity
		valueType = activity.valueType

		layoutTimeDistancePicker.valueView.text = activity.timeSeconds.toTimerString()
		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		timePopUp.currentFirst = "00"
		timePopUp.currentSecond = "00"

		layoutTimeDistancePicker.setOnClickListener {
			timePopUp.showPopupMenu()
		}

		imageButtonTimeDistanceDropdownIcon.makeInvisible()
		val resistancePopUp = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createResistanceValues()
		) { resistance ->

			logD("resistance $resistance")
			layoutPickerCardioAttribute1.valueView.text = resistance.toString()
			activity.resistance = resistance.toString().toInt()
		}

		textViewEditCardioAttribute1.text = getString(R.string.resistance_colon)
		layoutPickerCardioAttribute1.valueView.text = activity.resistance.toString()
		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			resistancePopUp.showPopupMenu()
		}


		val stepsPopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createStepsValues()
		) { steps ->

			logD("steps $steps")
			layoutPickerCardioAttribute2.valueView.text = steps.toString()
			activity.steps = steps.toString().toInt()
		}

		textViewEditCardioAttribute2.text = getString(R.string.steps_colon)
		val stepsString = activity.steps.toString()
		layoutPickerCardioAttribute2.valueView.text = stepsString
		stepsPopup.restorePickerState(stepsString)

		layoutPickerCardioAttribute2.setOnClickListener {
			stepsPopup.showPopupMenu()
		}

		val spmPopup = TwoPickersPopupWindow(
			layoutPickerCardioAttribute3,
			PickerValueFactory.createStepsSpmFirst(),
			"-",
			PickerValueFactory.createStepsSpmSecond()
		) { first, second ->
			val spm = "$first - $second"
			activity.spmFirst = first.toInt()
			activity.spmSecond = second.toInt()
			layoutPickerCardioAttribute3.valueView.text = spm
		}

		val spmString = "${activity.spmFirst} - ${activity.spmSecond}"
		textViewEditCardioAttribute3.text = getString(R.string.spm_asterisk_colon)
		layoutPickerCardioAttribute3.valueView.text = spmString

		spmPopup.restorePickerState(activity.spmFirst.toString(), activity.spmSecond.toString())
		layoutPickerCardioAttribute3.setOnClickListener {
			spmPopup.showPopupMenu()
		}

		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveRowingMachineLayout() {
		val activity = cardioActivity as RowingMachineActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createAlternativeFirstDistanceValues(),
			".",
			PickerValueFactory.createAlternativeSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}
			timeDistancePopup.showPopUp()
		}

		val intensityPopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createRowingMachineIntensity(requireContext())
		) { intensity ->

			logD("intensity $intensity")
			layoutPickerCardioAttribute1.valueView.text = intensity.toString()
			activity.intensity = RowingMachineActivity.Intensity.fromString(requireContext(), intensity.toString())
		}

		textViewEditCardioAttribute1.text = getString(R.string.intensity_colon)
		val intensityText = getString(activity.intensity.intensityRes)
		layoutPickerCardioAttribute1.valueView.text = intensityText
		intensityPopup.restorePickerState(intensityText)
		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			intensityPopup.showPopupMenu()
		}

		textViewMinsPerMeter.makeVisible()

		val pacePopUp = TwoPickersPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createRowingMachinePaceFirst(),
			":",
			PickerValueFactory.createRowingMachinePaceSecond()
		) { first, second ->
			logD("pace $first:$second")
			val pace = "$first:$second"
			activity.paceSeconds = pace.toSecond()
			layoutPickerCardioAttribute2.valueView.text = pace
		}

		textViewEditCardioAttribute2.text = getString(R.string.pace_colon)
		val timerText = activity.paceSeconds.toTimerString()
		layoutPickerCardioAttribute2.valueView.text = timerText
		pacePopUp.currentFirst = "00"
		pacePopUp.currentSecond = "00"
		pacePopUp.restorePickerState(timerText.substringBefore(":"), timerText.substringAfter(":"))
		layoutPickerCardioAttribute2.setOnClickListener {
			pacePopUp.showPopupMenu()
		}

		val spmPopup = TwoPickersPopupWindow(
			layoutPickerCardioAttribute3,
			PickerValueFactory.createRowingMachineSpmFirst(),
			"-",
			PickerValueFactory.createRowingMachineSpmSecond()
		) { first, second ->
			val spm = "$first - $second"
			activity.spmFirst = first.toInt()
			activity.spmSecond = second.toInt()
			layoutPickerCardioAttribute3.valueView.text = spm
		}

		val spmString = "${activity.spmFirst} - ${activity.spmSecond}"
		textViewEditCardioAttribute3.text = getString(R.string.spm_asterisk_colon)
		layoutPickerCardioAttribute3.valueView.text = spmString
		spmPopup.restorePickerState(activity.spmFirst.toString(), activity.spmSecond.toString())

		layoutPickerCardioAttribute3.setOnClickListener {
			spmPopup.showPopupMenu()
		}


		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveEllipticalLayout() {
		val activity = cardioActivity as EllipticalActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstDistanceValues(),
			".",
			PickerValueFactory.createSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}
			timeDistancePopup.showPopUp()
		}

		val resistancePopUp = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createResistanceValues()
		) { resistance ->

			logD("resistance $resistance")
			layoutPickerCardioAttribute1.valueView.text = resistance.toString()
			activity.resistance = resistance.toString().toInt()
		}

		textViewEditCardioAttribute1.text = getString(R.string.resistance_colon)
		val resistanceText = activity.resistance.toString()
		layoutPickerCardioAttribute1.valueView.text = resistanceText
		resistancePopUp.restorePickerState(resistanceText)

		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			resistancePopUp.showPopupMenu()
		}

		val inclinePopUp = TwoPickersPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createFirstInclineValues(),
			".",
			PickerValueFactory.createSecondInclineValues()
		) { first, second ->
			logD("Incline $first.$second")
			val incline = "$first.$second"
			layoutPickerCardioAttribute2.valueView.text = incline
			activity.incline = incline.toDouble()
		}

		textViewEditCardioAttribute2.text = getString(R.string.incline_colon)
		val inclineText = String.format(Locale.ENGLISH, "%.1f", activity.incline)
		layoutPickerCardioAttribute2.valueView.text = inclineText
		inclinePopUp.restorePickerState(inclineText.substringBefore("."), inclineText.substringAfter("."))
		layoutPickerCardioAttribute2.setOnClickListener {
			inclinePopUp.showPopupMenu()
		}

		val spmPopup = TwoPickersPopupWindow(
			layoutPickerCardioAttribute3,
			PickerValueFactory.createSPMFirst(),
			"-",
			PickerValueFactory.createSPMSecond()
		) { first, second ->

			activity.spmFirst = first.toInt()
			activity.spmSecond = second.toInt()

			val spmText = "$first - $second"
			layoutPickerCardioAttribute3.valueView.text = spmText
		}

		textViewEditCardioAttribute3.text = getString(R.string.spm_asterisk_colon)
		val spmText = "${activity.spmFirst} - ${activity.spmSecond}"
		layoutPickerCardioAttribute3.valueView.text = spmText
		spmPopup.currentFirst = "10"
		spmPopup.currentSecond = "20"
		spmPopup.restorePickerState(activity.spmFirst.toString(), activity.spmSecond.toString())
		layoutPickerCardioAttribute3.setOnClickListener {
			spmPopup.showPopupMenu()
		}

		saveBtn.setOnClickListener {onSave(activity) }
	}

	private fun resolveCyclingLayout() {
		val activity = cardioActivity as CyclingActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstDistanceValues(),
			".",
			PickerValueFactory.createSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}

			timeDistancePopup.showPopUp()
		}

		val resistancePopUp = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createResistanceValues()
		) { resistance ->

			logD("resistance $resistance")
			layoutPickerCardioAttribute1.valueView.text = resistance.toString()
			activity.resistance = resistance.toString().toInt()
		}

		textViewEditCardioAttribute1.text = getString(R.string.resistance_colon)
		val resistanceText = activity.resistance.toString()
		layoutPickerCardioAttribute1.valueView.text = resistanceText
		resistancePopUp.restorePickerState(resistanceText)

		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			resistancePopUp.showPopupMenu()
		}

		val rpmPopup = TwoPickersPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createRPMFirstValues(),
			"-",
			PickerValueFactory.createRPMSecondValues()
		) { first, second ->
			logD("RPM $first - $second")
			val rpmText = "$first - $second"
			activity.rpmFirst = first.toInt()
			activity.rpmSecond = second.toInt()
			layoutPickerCardioAttribute2.valueView.text = rpmText
		}

		textViewEditCardioAttribute2.text = getString(R.string.rpm_asterisk_colon)
		val rpm = "${activity.rpmFirst} - ${activity.rpmSecond}"
		layoutPickerCardioAttribute2.valueView.text = rpm
		layoutPickerCardioAttribute2.setOnClickListener {
			rpmPopup.showPopupMenu()
		}
		rpmPopup.currentFirst = "10"
		rpmPopup.currentSecond = "20"
		rpmPopup.restorePickerState(activity.rpmFirst.toString(), activity.rpmSecond.toString())

		textViewEditCardioAttribute3.makeInvisible()
		layoutPickerCardioAttribute3.makeInvisible()

		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveRunningLayout() {
		val activity = cardioActivity as RunningActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstDistanceValues(),
			".",
			PickerValueFactory.createSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}

			timeDistancePopup.showPopUp()
		}

		val speedPopUp = TwoPickersPopupWindow(
			layoutPickerCardioAttribute1,
			PickerValueFactory.createFirstSpeedValues(),
			".",
			PickerValueFactory.createSecondSpeedValues()
		) { first, second ->

			val speed = "$first.$second"
			val speedDouble = speed.toDouble()
			logD("speed $first.$second (double is $speedDouble)")
			layoutPickerCardioAttribute1.valueView.text = speed

			val paceDouble = 60.0 / speedDouble
			val paceString = String.format(Locale.ENGLISH, "%.2f", paceDouble)
			val integerPart = paceString.substringBefore(".")
			val decimalPart = paceString.substringAfter(".")
			try {
				val validDecimal = if (decimalPart.toInt() in 0..59) decimalPart.toInt() else 59
				val pace = integerPart.toInt() * 60 + validDecimal

				activity.speed = speedDouble
				activity.paceSeconds = pace
				layoutPickerCardioAttribute3.valueView.text =
					activity.paceSeconds.toTimerString()
			} catch (nfe: NumberFormatException) {
				activity.speed = speedDouble
				activity.paceSeconds = 0
				layoutPickerCardioAttribute3.valueView.text =
					activity.paceSeconds.toTimerString()
			}
		}

		textViewEditCardioAttribute1.text = getString(R.string.speed_colon)
		val speedText = String.format(Locale.ENGLISH, "%.1f", activity.speed)
		layoutPickerCardioAttribute1.valueView.text = speedText
		speedPopUp.restorePickerState(speedText.substringBefore("."), speedText.substringAfter("."))
		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on speed button")
			speedPopUp.showPopupMenu()
		}

		val inclinePopUp = TwoPickersPopupWindow(
			layoutPickerCardioAttribute2,
			PickerValueFactory.createFirstInclineValues(),
			".",
			PickerValueFactory.createSecondInclineValues()
		) { first, second ->
			logD("Incline $first.$second")
			val incline = "$first.$second"
			layoutPickerCardioAttribute2.valueView.text = incline
			activity.incline = incline.toDouble()
		}

		textViewEditCardioAttribute2.text = getString(R.string.incline_colon)
		val inclineText = String.format(Locale.ENGLISH, "%.1f", activity.incline)
		layoutPickerCardioAttribute2.valueView.text = inclineText
		inclinePopUp.restorePickerState(inclineText.substringBefore("."), inclineText.substringAfter("."))
		layoutPickerCardioAttribute2.setOnClickListener {
			inclinePopUp.showPopupMenu()
		}

		textViewEditCardioAttribute3.text = getString(R.string.pace_colon)
		layoutPickerCardioAttribute3.dropDownIcon.makeInvisible()
		layoutPickerCardioAttribute3.valueView.text = activity.paceSeconds.toTimerString()
		layoutPickerCardioAttribute3.valueView.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.gray_picker_color))

		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveJumpingRopeLayout() {
		val activity = cardioActivity as JumpingRopeActivity

		val timePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createFirstTimeValues(),
			":",
			PickerValueFactory.createSecondTimeValues()
		) { first, second ->
			logD("time $first:$second")
			val time = "$first:$second"
			activity.timeSeconds = time.toSecond()
			layoutTimeDistancePicker.valueView.text = time
		}

		val distancePopUp = TwoPickersPopupWindow(
			layoutTimeDistancePicker,
			PickerValueFactory.createAlternativeFirstDistanceValues(),
			".",
			PickerValueFactory.createAlternativeSecondDistanceValues()
		) { first, second ->
			logD("distance $first.$second")
			val distance = "$first.$second"
			layoutTimeDistancePicker.valueView.text = distance
			activity.distance = distance.toDouble()
		}

		valueType = activity.valueType
		resolveTimeDistance(activity.timeSeconds, timePopUp, activity.distance, distancePopUp)

		timeDistanceContainer.setOnClickListener {
			val timeDistancePopup =
				TimeDistancePopupWindow(
					imageButtonTimeDistanceDropdownIcon,
					activity.valueType
				) {
					if (activity.valueType == ValueType.TIME) {
						activity.valueType = ValueType.DISTANCE
						valueType = ValueType.DISTANCE
						textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					} else {
						activity.valueType = ValueType.TIME
						valueType = ValueType.TIME
						textViewTimeDistanceEdit.text = getString(R.string.time_colon)
						resolveTimeDistance(
							activity.timeSeconds,
							timePopUp,
							activity.distance,
							distancePopUp
						)
					}
				}
			timeDistancePopup.showPopUp()
		}

		val intensityPopup = OnePickerPopupWindow(
			layoutPickerCardioAttribute1,
			JumpingRopeActivity.Intensity.values().map { it.title() }
		) { intensity ->

			logD("intensity $intensity")
			layoutPickerCardioAttribute1.valueView.text = intensity.toString()
			activity.intensity = JumpingRopeActivity.Intensity.fromTitle(intensity.toString())
		}

		textViewEditCardioAttribute1.text = getString(R.string.intensity_colon)
		val intensityText = activity.intensity.title()
		layoutPickerCardioAttribute1.valueView.text = intensityText
		intensityPopup.restorePickerState(intensityText)
		layoutPickerCardioAttribute1.setOnClickListener {
			logD("click on resistance button")
			intensityPopup.showPopupMenu()
		}

		textViewEditCardioAttribute2.makeInvisible()
		layoutPickerCardioAttribute2.makeInvisible()
		textViewEditCardioAttribute3.makeInvisible()
		layoutPickerCardioAttribute3.makeInvisible()

		saveBtn.setOnClickListener { onSave(activity) }
	}

	private fun resolveTimeDistance(
		timeSeconds: Int,
		timePopUp: TwoPickersPopupWindow,
		distance: Double,
		distancePopUp: TwoPickersPopupWindow
	) {
		distancePopUp.currentSecond = "00"
		timePopUp.currentFirst = "00"
		timePopUp.currentSecond = "00"
		if (valueType == ValueType.TIME) {
			textViewTimeDistanceEdit.text = getString(R.string.time_colon)
			layoutTimeDistancePicker.valueView.text = timeSeconds.toTimerString()
			layoutTimeDistancePicker.setOnClickListener {
				logD("click on time button")
				timePopUp.showPopupMenu()
			}
			timePopUp.restorePickerState(timeSeconds.toTimerString().substringBefore(":"), timeSeconds.toTimerString().substringAfter(":"))
		} else {
			textViewTimeDistanceEdit.text = getString(R.string.distance_colon)
			val distanceText = String.format(Locale.ENGLISH, "%.2f", distance)
			layoutTimeDistancePicker.valueView.text = distanceText

			layoutTimeDistancePicker.setOnClickListener {
				logD("click on distance button")
				distancePopUp.showPopupMenu()
			}
			distancePopUp.restorePickerState(distanceText.substringBefore("."), distanceText.substringAfter("."))
		}
	}

	private fun onSave(activity: CardioActivity) {
		didEdit.invoke(cardioActivity)
		dismiss()
	}

}