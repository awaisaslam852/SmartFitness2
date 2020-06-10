package com.axles.smartfitness.ui.timer

import android.os.Bundle
import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.profile.edit.ParentInteractor
import kotlinx.android.synthetic.main.duration_dialog.*

class DurationDialog(
    private val minutes: Int,
    private val seconds: Int,
    private val passRestTime: (Int, Int) -> Unit,
    private val parentInteractor: ParentInteractor
) : BaseFragment(R.layout.duration_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutEditTransparent.setOnClickListener { }
        numberPickerMinutes.apply {
            minValue = 0
            maxValue = 59
            value = minutes
        }
        numberPickerSeconds.apply {
            minValue = 0
            maxValue = 59
            value = seconds
        }
        cancelBtn.setOnClickListener {
            parentInteractor.closeCurrent()
        }
        saveBtn.setOnClickListener {
            val minutes = numberPickerMinutes.value
            val seconds = numberPickerSeconds.value
            passRestTime(minutes, seconds)
            parentInteractor.closeCurrent()
        }
    }
}