package com.axles.smartfitness.ui.premium

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.premium_fragment.*

class PremiumFragment : BaseFragment(R.layout.premium_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        closeFragment.setOnClickListener { findNavController().popBackStack() }
    }
}