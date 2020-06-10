package com.axles.smartfitness.ui.timer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.max

class TimerViewModel : ViewModel() {
	enum class Status {
		COMPLETED,
		STOPPED,
		PAUSED,
		RUNNING,
	}

	private var timerDisposable: Disposable = CompositeDisposable()
	val duration = MutableLiveData(45)
	val currentTime = MutableLiveData(duration.value)
	val status = MutableLiveData(Status.STOPPED)
	private val diffTime = 5

	fun startTimer() {
		status.postValue(Status.RUNNING)
		timerDisposable = Observable
			.interval(1L, TimeUnit.SECONDS)
			.doOnNext {
				if (currentTime.value == 0) {
					timerDisposable.dispose()
					status.postValue(Status.COMPLETED)
					return@doOnNext
				}
				currentTime.postValue(currentTime.value?.minus(1))
			}
			.subscribe()
	}

	fun togglePauseTimer() {
		when (status.value) {
			Status.RUNNING -> pauseTimer()
			Status.PAUSED -> startTimer()
			else -> {}
		}
	}

	private fun pauseTimer() {
		status.postValue(Status.PAUSED)
		timerDisposable.dispose()
	}

	fun stopTimer() {
		status.postValue(Status.STOPPED)
		timerDisposable.dispose()
		currentTime.postValue(duration.value)
	}

	fun plus() {
		val currentDuration = duration.value ?: return
		duration.postValue(currentDuration+diffTime)
		currentTime.postValue(currentTime.value!!+diffTime)
	}

	fun minus() {
		val currentDuration = duration.value ?: return
		duration.postValue(max(currentDuration-diffTime, diffTime))
		currentTime.postValue(max(currentTime.value!!-diffTime, diffTime))
	}

	fun setDuration(duration: Int) {
		this.duration.postValue(duration)
		this.currentTime.postValue(duration)
	}
}