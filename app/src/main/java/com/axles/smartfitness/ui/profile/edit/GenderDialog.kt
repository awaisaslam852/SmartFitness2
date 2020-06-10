package com.axles.smartfitness.ui.profile.edit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.annotation.IdRes
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.FloatingDialog
import com.axles.smartfitness.engine.user.User.Gender

class GenderDialog(
    private val gender: Gender,
    private val selectGender: (Gender) -> Unit
) : FloatingDialog() {
    private lateinit var radioButtons: List<RadioButton>

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val layoutInflater = LayoutInflater.from(it)
            val view = layoutInflater.inflate(R.layout.choose_gender_dialog, null).apply {
                findViewById<ImageButton>(R.id.imageButtonDismiss).setOnClickListener {
                    this@GenderDialog.dismiss()
                }
                radioButtons = listOf(
                    findRadioButtonWith(R.id.maleRadioButton, Gender.Male),
                    findRadioButtonWith(R.id.femaleRadioButton, Gender.Female)
                )
                findViewById<View>(R.id.maleImageView).setOnClickListener {
                    radioButtons[0].performClick()
                }
                findViewById<View>(R.id.femaleImageView).setOnClickListener {
                    radioButtons[1].performClick()
                }
                initViewWithValues()
            }

            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initViewWithValues() {
        selectGender(gender)
        if (gender == Gender.Male) {
            radioButtons[0].isSelected = true
        } else {
            radioButtons[1].isSelected = true
        }
    }

    private fun View.findRadioButtonWith(@IdRes id: Int, gender: Gender) =
        findViewById<RadioButton>(id).apply {
            setOnClickListenerWith(gender)
        }

    private fun View.setOnClickListenerWith(gender: Gender) {
        setOnClickListener {
            selectGender(gender)
            dismissDelayed(300)
        }
    }
}