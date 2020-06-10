package com.axles.smartfitness.engine

import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.axles.smartfitness.R
import com.axles.smartfitness.app.SmartFitnessApplication

class AudioPlayer {
	companion object {
		val instance = AudioPlayer()
	}

	private var mediaPlayers = mutableListOf<MediaPlayer>()

	private fun startPlay(@RawRes resId: Int) {
		val mediaPlayer = MediaPlayer.create(SmartFitnessApplication.instance, resId)
		mediaPlayer.setOnPreparedListener { mediaPlayer.start() }
		mediaPlayer.setOnCompletionListener { mediaPlayers.remove(mediaPlayer) }
		mediaPlayers.add(mediaPlayer)
	}

	fun playExerciseTimerBeep() {
		startPlay(R.raw.main_timer)
	}

	fun playRestTimerBeep() {
		startPlay(R.raw.rest_timer)
	}
}