package com.axles.smartfitness.ui.login

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.axles.smartfitness.R
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.web_view_fragment.*

class WebViewFragment: BaseFragment(R.layout.web_view_fragment) {
	private val args: WebViewFragmentArgs by navArgs()

	private lateinit var url: String

	override fun resolveArguments() {
		url = args.url
	}

	override fun init() {
		topBarBackBtn.setOnClickListener { findNavController().navigateUp() }
	}

	override fun update() {
		webView.loadUrl(url)
	}
}