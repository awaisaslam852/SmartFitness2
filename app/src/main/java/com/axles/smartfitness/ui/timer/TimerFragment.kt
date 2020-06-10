package com.axles.smartfitness.ui.timer

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.AudioPlayer
import com.axles.smartfitness.engine.program.Program
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.extensions.observe
import com.axles.smartfitness.extensions.toTimerString
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.profile.edit.ParentInteractor
import kotlinx.android.synthetic.main.timer_fragment.*

class TimerFragment : BaseFragment(R.layout.timer_fragment), ParentInteractor {
	private val args: TimerFragmentArgs by navArgs()
	private lateinit var timerViewModel: TimerViewModel
	private lateinit var program: Program

	private enum class TimerType {
		Exercise,
		Rest
	}
	private var timerType = TimerType.Exercise
		set(value) {
			field = value
			if (value == TimerType.Exercise)
				timerViewModel.setDuration(exerciseDuration)
			else
				timerViewModel.setDuration(restDuration)
		}
	private var exerciseDuration = 45
		set(value) {
			field = value
			resetExerciseTimer()
		}
	private var restDuration = 30
		set(value) {
			field = value
			resetRestTimer()
		}
	private var repeatCount = 2
		set(value) {
			field = value
			resetRepeatCount()
		}
	private var currentRepeatIndex = 0
		set(value) {
			field = value
			repeatValue.text = getString(R.string.times_placeholder, repeatCount-currentRepeatIndex)
		}
	private var autoRepeat = false
		set(value) {
			field = value

			autoRepeatCheckBox.setImageResource(if (autoRepeat) R.drawable.check_box_selected else R.drawable.check_box_not_selected)
		}

	override fun resolveArguments() {
		program = args.program
	}

	override fun init() {
		timerPlus.setOnClickListener { timerViewModel.plus() }
		timerMinus.setOnClickListener { timerViewModel.minus() }
		startButton.setOnClickListener { timerViewModel.startTimer() }

		pauseButton.setOnClickListener { timerViewModel.togglePauseTimer() }
		stopButton.setOnClickListener {timerViewModel.stopTimer() }
		timerChat.setOnClickListener {
			val duration = timerViewModel.duration.value ?: return@setOnClickListener
			val minutes = (duration / 60)
			val seconds = (duration % 60)
			openPopUpFragment(
				DurationDialog(minutes, seconds, ::setDuration, this),
				R.id.root
			)
		}
		restValue.text = restDuration.toTimerString()
		restValue.setOnClickListener {
			val minutes = restDuration / 60
			val seconds = restDuration % 60
			openPopUpFragment(
				RestDialog(minutes, seconds, ::setRestTime, this),
				R.id.root
			)
		}

		repeatValue.text = getString(R.string.times_placeholder, repeatCount)
		repeatValue.setOnClickListener {
			openPopUpFragment(
				RepeatDialog(repeatCount, ::setRepeat, this),
				R.id.root
			)
		}
		autoRepeatCheckBox.setOnClickListener { autoRepeat = !autoRepeat }
//		openPremium.setOnClickListener { findNavController().navigate(R.id.premiumFragment) }

		timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
		timerViewModel.duration.observe(viewLifecycleOwner, Observer {
			when (timerType) {
				TimerType.Exercise -> {
					exerciseDuration = it
					timerChat.setMax(it)
				}
				TimerType.Rest -> {
					restDuration = it
					restValue.text = it.toTimerString()
				}
			}
		})
		timerViewModel.currentTime.observe(viewLifecycleOwner, Observer { updateTimer(it!!) })
		timerViewModel.status.observe(this, ::onStateChanged)

		reset()
	}

	private fun reset() {
		timerType = TimerType.Exercise
		resetExerciseTimer()
		currentRepeatIndex = 0
	}

	private fun resetExerciseTimer() {
		remainingTimeView.text = exerciseDuration.toTimerString()
		timerChat.setMax(exerciseDuration)
		timerChat.reset()
	}

	private fun resetRestTimer() {
		restValue.text = restDuration.toTimerString()
	}

	private fun resetRepeatCount() {
		repeatValue.text = getString(R.string.times_placeholder, repeatCount)
	}

	private fun onStateChanged(status: TimerViewModel.Status) {
		when (status) {
			TimerViewModel.Status.COMPLETED -> {
				startButton.makeVisible()
				pauseButton.makeGone()
				stopButton.makeGone()

				when (timerType) {
					TimerType.Exercise -> {
						AudioPlayer.instance.playExerciseTimerBeep()
						resetExerciseTimer()
						if (autoRepeat) {
							timerType = TimerType.Rest
							timerViewModel.startTimer()
						}
					}
					TimerType.Rest -> {
						AudioPlayer.instance.playRestTimerBeep()
						resetRestTimer()
						currentRepeatIndex++
						if (currentRepeatIndex < repeatCount) {
							timerType = TimerType.Exercise
							if (autoRepeat) timerViewModel.startTimer()
							return
						}

						timerViewModel.stopTimer()
						reset()
					}
				}
			}
			TimerViewModel.Status.STOPPED -> {
				startButton.makeVisible()
				pauseButton.makeGone()
				stopButton.makeGone()
				reset()
			}
			TimerViewModel.Status.PAUSED -> {
				pauseTitle.text = getString(R.string.start)
				pauseIcon.setImageDrawable(requireContext().getDrawable(R.drawable.ic_play))
			}
			TimerViewModel.Status.RUNNING -> {
				startButton.makeGone()
				pauseButton.makeVisible()
				stopButton.makeVisible()

				pauseTitle.text = getString(R.string.pause)
				pauseIcon.setImageDrawable(requireContext().getDrawable(R.drawable.ic_pause))
			}
		}
	}

	private fun updateTimer(rawSeconds: Int) {
		when (timerType) {
			TimerType.Exercise -> {
				remainingTimeView.text = rawSeconds.toTimerString()
				timerChat.setProgress(rawSeconds.toFloat())
			}
			TimerType.Rest -> {
				restValue.text = rawSeconds.toTimerString()
			}
		}
	}

	private fun setRestTime(minutes: Int, seconds: Int) {
		val formattedTime = buildString {
			fun appendIfLessTen(value: Int) {
				if (value < 10) {
					append(0)
				}
				append(value)
			}
			appendIfLessTen(minutes)
			append(":")
			appendIfLessTen(seconds)
		}
		restValue.text = formattedTime
		restDuration = minutes * 60 + seconds
		autoRepeat = true
	}

	private fun setDuration(minutes: Int, seconds: Int) {
		exerciseDuration = minutes * 60 + seconds
		timerViewModel.setDuration(minutes * 60 + seconds)
		autoRepeat = true
	}

	private fun setRepeat(repeat: Int) {
		repeatCount = repeat
		autoRepeat = true
	}

	override fun closeCurrent() {
		childFragmentManager.popBackStack()
	}
}

