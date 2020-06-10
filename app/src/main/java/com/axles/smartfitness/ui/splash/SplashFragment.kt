package com.axles.smartfitness.ui.splash

import android.os.Handler
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.network.session.SessionManagerFactory
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.ui.base.BaseFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.splash_fragment.*

class SplashFragment: BaseFragment(R.layout.splash_fragment) {

    companion object {
        private const val SPLASH_DURATION = 3000L
    }

    override fun init() {
        setupLoaderGif()
        Handler().postDelayed({
            resolveNavigation()
        }, SPLASH_DURATION)
    }

    private fun resolveNavigation() {
        if (UserManager.hasLoggedIn()) {
            navController.navigate(R.id.actionSplashToMain)
        } else {
            navController.navigate(R.id.splashToLogin)
        }
    }

    private fun setupLoaderGif() {
        Glide.with(this.requireContext()).asGif().load(R.drawable.gif).into(imageViewLoaderGif)
    }
}