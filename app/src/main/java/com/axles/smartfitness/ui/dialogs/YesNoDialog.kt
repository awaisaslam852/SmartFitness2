package com.axles.smartfitness.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import java.lang.IllegalStateException

class YesNoDialog(private val message: String, var yesNoListener: ((Boolean) -> Unit)?) : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = View.inflate(context, R.layout.yes_no_dialog, null)

            view.findViewById<TextView>(R.id.textViewMessage).text = message

            val dismissButton = view.findViewById<ImageButton>(R.id.closeBtn)
            dismissButton.setOnClickListener {
                this.dismiss()
            }

            val yesButton = view.findViewById<Button>(R.id.buttonYes)
            yesButton.setOnClickListener {
                yesNoListener?.invoke(true)
                this.dismiss()
                logD("yes button clicked")
            }

            val noButton = view.findViewById<Button>(R.id.buttonNo)
            noButton.setOnClickListener {
                yesNoListener?.invoke(false)
                this.dismiss()
                logD("no button clicked")
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}