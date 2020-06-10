package com.axles.smartfitness.ui.cardio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.makeGone
import kotlinx.android.synthetic.main.help_cardio_fragment.*
import kotlinx.android.synthetic.main.cardio_multiplication_layout.*
import kotlinx.android.synthetic.main.toolbar_help_cardio.*

class HelpCardioFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.help_cardio_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBarBackBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        buttonOkHelp.setOnClickListener {
            findNavController().navigateUp()
        }

        numberPickerMultiply.minValue = 2
        numberPickerMultiply.maxValue = 10
        numberPickerMultiply.value = 3
        numberPickerMultiply.isEnabled = false
        buttonMultiplyOk.isEnabled = false
        buttonMultiplyOk.makeGone()
        textViewCancelMultiplication.makeGone()

    }
}