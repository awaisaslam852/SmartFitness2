package com.axles.smartfitness.ui.profile.edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.annotation.IdRes
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.user.User.FitnessLevel
import com.axles.smartfitness.ui.base.FloatingDialog

class FitnessLevelDialog(
    private val initialFitnessLevel: FitnessLevel,
    private val selectLevel: (FitnessLevel) -> Unit
) : FloatingDialog() {
    private lateinit var radioButtons: List<RadioButton>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val layoutInflater = LayoutInflater.from(it)


            val view = layoutInflater.inflate(R.layout.choose_fitness_level_dialog, null)

            view.apply {
                findViewById<ImageButton>(R.id.imageButtonDismiss).setOnClickListener {
                    this@FitnessLevelDialog.dismiss()
                }
                radioButtons = listOf(
                    findRadioButtonWith(R.id.beginnerRadioButton, FitnessLevel.Beginner),
                    findRadioButtonWith(R.id.intermediateRadioButton, FitnessLevel.Intermediate),
                    findRadioButtonWith(R.id.expertRadioButton, FitnessLevel.Expert)
                )
                findViewById<View>(R.id.beginnerImageView).setOnClickListener {
                    radioButtons[0].performClick()
                }
                findViewById<View>(R.id.intermediateImageView).setOnClickListener {
                    radioButtons[1].performClick()
                }
                findViewById<View>(R.id.expertImageView).setOnClickListener {
                    radioButtons[2].performClick()
                }

                selectLevel(initialFitnessLevel)
                when (initialFitnessLevel) {
                    FitnessLevel.Beginner -> radioButtons[0].isSelected = true
                    FitnessLevel.Intermediate -> radioButtons[1].isSelected = true
                    FitnessLevel.Expert -> radioButtons[2].isSelected = true
                }
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun View.findRadioButtonWith(@IdRes id: Int, fitnessLevel: FitnessLevel) =
        findViewById<RadioButton>(id).apply {
            setOnClickListener {
                selectLevel(fitnessLevel)
                dismissDelayed(300)
            }
        }
}