package com.axles.smartfitness.ui.profile.edit

import android.os.Bundle
import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.format
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.choose_height_dialog.layoutEditTransparent
import kotlinx.android.synthetic.main.choose_weight_dialog.*

class WeightDialog(
    private val initWeight: Double?,
    private val selectWeight: (Double) -> Unit,
    private val parentInteractor: ParentInteractor
) : BaseFragment(R.layout.choose_weight_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutEditTransparent.setOnClickListener { }
        val weightParts = initWeight?.format(2)?.split(".", ",")
        numberPickerKilograms.apply {
            minValue = 0
            maxValue = 300
            weightParts?.get(0)?.toInt()?.let {
                value = it
            }
        }
        numberPickerKgFractions.apply {
            minValue = 0
            maxValue = 99
            weightParts?.get(1)?.toInt()?.let {
                value = it
            }
        }
        cancelBtn.setOnClickListener {
            parentInteractor.closeCurrent()
        }
        saveBtn.setOnClickListener {
            val weight = "${numberPickerKilograms.value}.${numberPickerKgFractions.value}"
            selectWeight(weight.toDouble())
            parentInteractor.closeCurrent()
        }
    }
}