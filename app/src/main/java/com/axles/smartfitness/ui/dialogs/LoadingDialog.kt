package com.axles.smartfitness.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseDialogFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_loading.view.*

class LoadingDialog: BaseDialogFragment(R.layout.fragment_loading) {
	override fun init(rootView: View) {
		super.init(rootView)

		requireDialog().window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

		with (rootView) {
			Glide.with(requireContext()).asGif().load(R.drawable.gif).into(gifView)
		}

		isCancelable = false
	}
}