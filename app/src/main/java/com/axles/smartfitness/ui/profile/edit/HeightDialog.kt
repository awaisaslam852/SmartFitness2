package com.axles.smartfitness.ui.profile.edit

import android.os.Bundle
import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.choose_height_dialog.*

class HeightDialog(
    private val initialHeight: Double?,
    private val selectHeight: (Int) -> Unit,
    private val parentInteractor: ParentInteractor
) : BaseFragment(R.layout.choose_height_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutEditTransparent.setOnClickListener { }
        numberPickerHeight.apply {
            minValue = 130
            maxValue = 220
            initialHeight?.let {
                value = it.toInt()
            }
        }
        cancelBtn.setOnClickListener {
            parentInteractor.closeCurrent()
        }
        saveBtn.setOnClickListener {
            selectHeight(numberPickerHeight.value)
            parentInteractor.closeCurrent()
        }
    }
}