package com.axles.smartfitness.ui.about

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.about_fragment.*
import kotlinx.android.synthetic.main.toolbar_about.*

class AboutFragment : BaseFragment(R.layout.about_fragment) {
    override fun init() {
        imageButtonBurger.setOnClickListener {
            openDrawer()
        }

        termsBtn.setOnClickListener {
            val action = AboutFragmentDirections.toWebView(Constants.termsOfServiceUrl)
            findNavController().navigate(action)
        }

        privacyPolicyBtn.setOnClickListener {
            val action = AboutFragmentDirections.toWebView(Constants.privacyPolicyUrl)
            findNavController().navigate(action)
        }
    }
}