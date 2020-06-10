package com.axles.smartfitness.ui.resistance.exercise_details

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.resistance.exercise.Exercise
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.ui.base.BaseFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.exercise_details_fragment.*

class ExerciseDetailFragment : BaseFragment(R.layout.exercise_details_fragment) {
	private val args: ExerciseDetailFragmentArgs? by navArgs()

	private lateinit var exercise : Exercise
	private var showNextImage = true
	private var currentImageIndex = 0

	override fun resolveArguments() {
		if (args == null) { return }
		exercise = args!!.exercise
	}

	override fun init() {
		imageButtonExerciseBack.setOnClickListener {
			findNavController().navigateUp()
		}

		nextDescriptionBtn.setOnClickListener {
			val descriptionCount = exercise.descriptions.size
			val descriptionIndex = detailViewPager.currentItem
			if (descriptionIndex < descriptionCount-1) {
				detailViewPager.currentItem = descriptionIndex + 1
			}
		}

		prevDescriptionBtn.setOnClickListener {
			val descriptionIndex = detailViewPager.currentItem
			if (descriptionIndex > 0) {
				detailViewPager.currentItem = descriptionIndex - 1
			}
		}

		detailViewPager.adapter = ExerciseDescriptionViewPagerAdapter(exercise)
		detailViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				update()
			}
		})

		setupWatchVideo()
		setupImageSwitcher()
	}

	override fun update() {
		textViewExerciseTittle.text = exercise.title

		val descriptionCount = exercise.descriptions.size
		val descriptionIndex = detailViewPager.currentItem
		descriptionIndexView.text = "${descriptionIndex+1}/$descriptionCount"

		if (descriptionIndex <= 0) { prevDescriptionBtn.setColorFilter(Helper.color(R.color.gray)) }
		else { prevDescriptionBtn.setColorFilter(Helper.color(R.color.white)) }

		if (descriptionIndex >= descriptionCount) { nextDescriptionBtn.setColorFilter(Helper.color(R.color.gray)) }
		else { nextDescriptionBtn.setColorFilter(Helper.color(R.color.white)) }
	}

	private fun setupImageSwitcher() {
		if (exercise.exerciseImages.isNotEmpty()) {
			Glide.with(requireContext())
				.load(exercise.exerciseImages[currentImageIndex])
				.into(imageSwitcher.currentView as ImageView)
			startDelayedImageSwitch()
		}
	}

	private fun startDelayedImageSwitch(){
		Handler().postDelayed({
			if (currentImageIndex + 1 < exercise.exerciseImages.size){
				currentImageIndex++
			} else {
				currentImageIndex = 0
			}
			if (showNextImage) {
				Glide.with(requireContext())
					.load(exercise.exerciseImages[currentImageIndex])
					.into(imageSwitcher.nextView as ImageView)

				requireActivity().runOnUiThread {
					imageSwitcher.showNext()
				}

				startDelayedImageSwitch()
			}
		}, IMAGE_SWITCH_DELAY)
	}

	private fun setupWatchVideo() {
		buttonWatchVideo.setOnClickListener {
			val intent = Intent(Intent.ACTION_VIEW).apply {
				setDataAndType(Uri.parse(exercise.videoUrl), "video/mp4")
			}

			startActivity(intent)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		logD("view destroyed")
		this.showNextImage = false
	}

	companion object {
		const val IMAGE_SWITCH_DELAY = 1000L
	}
}