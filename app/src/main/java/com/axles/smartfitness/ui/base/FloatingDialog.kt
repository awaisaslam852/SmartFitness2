package com.axles.smartfitness.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

abstract class FloatingDialog : DialogFragment() {
    override fun onStart() {
        super.onStart()
        setupDialog()
    }

    private fun setupDialog() {
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    @Suppress("SameParameterValue")
    protected fun dismissDelayed(delay: Long) {
        Handler().postDelayed({ dismiss() }, delay)
    }
}