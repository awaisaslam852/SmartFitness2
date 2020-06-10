package com.axles.smartfitness.ui.timer

import android.os.Bundle
import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.profile.edit.ParentInteractor
import kotlinx.android.synthetic.main.repeat_dialog.*
import kotlinx.android.synthetic.main.rest_dialog.layoutEditTransparent
import kotlinx.android.synthetic.main.rest_dialog.cancelBtn
import kotlinx.android.synthetic.main.rest_dialog.saveBtn

class RepeatDialog(
    private val times: Int,
    private val passTimes: (Int) -> Unit,
    private val parentInteractor: ParentInteractor
) : BaseFragment(R.layout.repeat_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutEditTransparent.setOnClickListener { }
        numberPickerTimes.apply {
            minValue = 2
            maxValue = 99
            value = times
        }
        cancelBtn.setOnClickListener {
            parentInteractor.closeCurrent()
        }
        saveBtn.setOnClickListener {
            passTimes(numberPickerTimes.value)
            parentInteractor.closeCurrent()
        }
    }
}