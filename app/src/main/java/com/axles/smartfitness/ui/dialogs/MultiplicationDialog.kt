package com.axles.smartfitness.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import kotlinx.android.synthetic.main.cardio_multiplication_dialog.view.*
import java.lang.IllegalStateException

class MultiplicationDialog(var onChoose: ((count: Int) -> Unit)?) : DialogFragment(){

    var currentValue = 2

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = View.inflate(context, R.layout.cardio_multiplication_dialog, null)

            val values = List(9){
                it + 2
            }.map {
                it.toString()
            }

            val numberPicker = view.numberPickerMultiply
            numberPicker.isFadingEdgeEnabled = true
            numberPicker.minValue = 0
            numberPicker.maxValue = values.size - 1
            numberPicker.value = 0
            numberPicker.displayedValues = values.toTypedArray()
            numberPicker.setOnValueChangedListener { _, _, newVal ->
                currentValue = values[newVal].toInt()
            }

            val textViewCancel = view.textViewCancelMultiplication
            textViewCancel.setOnClickListener {
                this.dismiss()
            }

            val okButton = view.buttonMultiplyOk
            okButton.setOnClickListener {
                onChoose?.invoke(currentValue)
                this.dismiss()
                logD("yes button clicked")
            }


            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}