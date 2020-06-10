package com.axles.smartfitness.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.extensions.load
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.observeWrapper
import com.axles.smartfitness.localization.ContextWrapper
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.ui.main.MainViewModel
import com.axles.smartfitness.ui.main.NavigationDrawerManager
import com.phelat.navigationresult.FragmentResultActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view_content.*


class MainActivity : FragmentResultActivity() {
	private lateinit var viewModel: MainViewModel
	private lateinit var navHost: NavController

	override fun getNavHostFragmentId(): Int = R.id.nav_host_fragment

	override fun onCreate(savedInstanceState: Bundle?) {
		resolveLocalization()
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
		subscribeVM()
		logD("main activity created")
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		val loginFragment = SmartFitnessApplication.instance.loginFragment
		loginFragment?.onActivityResult(requestCode, resultCode, data)
	}

	override fun onResume() {
		resolveDisplayMetrics()
		super.onResume()
		initNavHost()
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		resolveDisplayMetrics()
		super.onConfigurationChanged(newConfig)
	}

	private var backPressCount: Int = 0
	override fun onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START)
			return
		}

		if (navHost.currentDestination!!.id == R.id.mainFragment) {
			if (backPressCount == 0) {
				backPressCount++
				Helper.showToast(this, R.string.press_again_to_exit)
				return
			}
			finish()
			return
		}

		if (navHost.currentDestination!!.id == R.id.resistanceTrainingFragment
			&& SmartFitnessApplication.instance.resistanceTrainingFragment != null) {
			SmartFitnessApplication.instance.resistanceTrainingFragment!!.onBackPressed()
			return
		}

		super.onBackPressed()
	}

	private fun initNavHost() {
		navHost = findNavController(this, R.id.nav_host_fragment)
		navHost.addOnDestinationChangedListener { _, destination, _ ->
			resolveDisplayMetrics()
			backPressCount = 0
			val destinations = listOf(
				R.id.mainFragment
			)
			if (destination.id in destinations) {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
				setupNavigationDrawer()
				viewModel.fetchUser()
			} else {
				drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
			}
		}
	}

	private fun subscribeVM() {
		viewModel.profile.observeWrapper(
			this,
			{
				imageViewUserAvatar.load(it.imageUrl)
				textViewUsername.text = it.username
			}
		)
	}

	fun openDrawer() {
		drawerLayout.openDrawer(GravityCompat.START)
	}

	private fun setupNavigationDrawer() {
		if (!this::navHost.isInitialized) { initNavHost() }
		val navigationDrawerSetup = NavigationDrawerManager(this, navHost)
		navigationDrawerSetup.setupListeners()
	}

	private fun resolveDisplayMetrics() {
		val appConfig = Configuration(applicationContext.resources.configuration)
		appConfig.fontScale = 1.toFloat() //0.85 small size, 1 normal size, 1,15 big etc
		val metrics = DisplayMetrics()
		windowManager.defaultDisplay.getMetrics(metrics)
		metrics.scaledDensity = appConfig.fontScale * metrics.density
		appConfig.densityDpi = resources.displayMetrics.xdpi.toInt()
		applicationContext.resources.updateConfiguration(appConfig, metrics)
	}

	private fun resolveLocalization() {
		LocalizationSettingsManager.updateConfiguration(this, resources.configuration)
	}
}
