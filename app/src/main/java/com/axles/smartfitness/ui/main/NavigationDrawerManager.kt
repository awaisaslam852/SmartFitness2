package com.axles.smartfitness.ui.main

import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.activity.MainActivity
import com.axles.smartfitness.ui.dialogs.ChooseLanguageDialog
import com.axles.smartfitness.ui.dialogs.OkDialog
import com.axles.smartfitness.ui.dialogs.YesNoDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view_content.*


class NavigationDrawerManager(
	private val mainActivity: MainActivity,
	private val navController: NavController
) {
	private var currentFragmentTag = HOME_TAG
		set(value) {
			field = value
			resolveDrawerState()
		}

	val fragmentManager: FragmentManager
		get() = mainActivity.supportFragmentManager

	fun getString(@StringRes id: Int) = mainActivity.getString(id)

	private fun resolveDrawerState() {
		setAllTextViewDefaultTypeface()
		when (currentFragmentTag) {
			HOME_TAG -> selectHome()
			PROGRAMS_TAG -> selectPrograms()
			ABOUT_TAG -> selectAbout()
			PROFILE_TAG -> selectProfile()
		}
	}

	private fun selectHome() {
		mainActivity.textViewNavigationHome.setTypeFace(R.font.os_bold)
		mainActivity.selectedBarHome.makeVisible()
	}

	private fun selectPrograms() {
		mainActivity.textViewNavigationPrograms.setTypeFace(R.font.os_bold)
		mainActivity.selectedBarPrograms.makeVisible()
	}

	private fun selectAbout() {
		mainActivity.textViewNavigationAbout.setTypeFace(R.font.os_bold)
		mainActivity.selectedBarAbout.makeVisible()
	}

	private fun selectProfile() {
//		mainActivity.textViewUsername.setTypeFace(R.font.os_bold)
		mainActivity.userSectionSelected.makeVisible()
	}

	private fun setAllTextViewDefaultTypeface() {
		with(mainActivity) {
			textViewNavigationHome.setTypeFace(R.font.os)
			textViewNavigationAbout.setTypeFace(R.font.os)
			textViewNavigationLanguages.setTypeFace(R.font.os)
			textViewNavigationShare.setTypeFace(R.font.os)
			textViewNavigationFeedback.setTypeFace(R.font.os)
			textViewNavigationGPS.setTypeFace(R.font.os)
			textViewNavigationTrack.setTypeFace(R.font.os)
			textViewNavigationPrograms.setTypeFace(R.font.os)
			textViewNavigationLogout.setTypeFace(R.font.os)

			selectedBarAbout.makeInvisible()
			selectedBarHome.makeInvisible()
			selectedBarPrograms.makeInvisible()
			userSectionSelected.makeInvisible()
		}
	}

	private fun TextView.setTypeFace(@FontRes font: Int) {
		typeface = ResourcesCompat.getFont(context, font)
	}

	private fun goLanguages() {
		close()
		ChooseLanguageDialog()
			.show(fragmentManager, LANGUAGE_TAG)
	}

	private fun goTrack() {
		this.close()
		OkDialog(getString(R.string.coming_soon)).show(fragmentManager, COMING_SOON_TAG)
	}

	private fun close() {
		mainActivity.drawerLayout.closeDrawer(GravityCompat.START)
	}

	private fun goGpsRunning() {
		goTrack()
	}

	private fun onFeedback() {
		val body = "System info App v${Helper.versionNumber()}, model ${Helper.deviceModel()}"
		val intent = Intent(Intent.ACTION_SENDTO)
		val uriText = "mailto:" + Uri.encode(Constants.contactEmail) +
				"?subject=" + Uri.encode("Smart Fitness Feedback") +
				"&body=" + Uri.encode(body)
		val uri = Uri.parse(uriText)
		intent.data = uri

		with (mainActivity) {
			try {
				startActivity(Intent.createChooser(intent, "Send mail..."))
			} catch (e: Exception) {
				Helper.showErrorToast(this, e.localizedMessage)
			}

			drawerLayout.closeDrawer(GravityCompat.START)
		}
	}

	private fun onShare() {
		with (mainActivity) {
			val subject = "Check out Smart Fitness app"
			val body = "Check this app! Smart Fitness\n\nDownload the app: ${Helper.googlePlayStoreLink()}"
			val sharingIntent = Intent(Intent.ACTION_SEND)
			sharingIntent.type = "text/plain"
			val shareBody = Helper.googlePlayStoreLink()
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
			sharingIntent.putExtra(Intent.EXTRA_TEXT, body)
			try {
				startActivity(Intent.createChooser(sharingIntent, subject))
			} catch (e: Exception) {
				Helper.showErrorToast(this, e.localizedMessage)
			}

			drawerLayout.closeDrawer(GravityCompat.START)
		}
	}

	private fun goLogout() {
		close()
		val yesNoListener = { yes: Boolean ->
			if (yes) {
				UserManager.logout()
				navController.navigate(R.id.toLoginMethodFragment)
			}
		}
		YesNoDialog(getString(R.string.are_you_sure_logout), yesNoListener)
			.show(fragmentManager, LOGOUT_DIALOG_TAG)
	}

	fun setupListeners() {
		resolveDrawerState()
		with(mainActivity) {
			imageButtonCloseNavigation.setOnClickListener {
				drawerLayout.closeDrawer(GravityCompat.START)
			}
			userSection.setOnClickListener {
				navController.navigate(R.id.profileFragment, null, provideNavOptions())
				currentFragmentTag = PROFILE_TAG
			}
			homeContainer.setOnClickListener {
				navController.navigate(R.id.mainFragment, null, provideNavOptions())
				currentFragmentTag = HOME_TAG
				drawerLayout.closeDrawer(GravityCompat.START)
			}
			textViewNavigationLogout.setOnClickListener {
				goLogout()
			}
			imageButtonIconLogout.setOnClickListener {
				goLogout()
			}
			textViewNavigationLanguages.setOnClickListener {
				goLanguages()
			}
			imageButtonIconLanguages.setOnClickListener {
				goLanguages()
			}
			textViewNavigationPrograms.setOnClickListener {
				navController.navigate(R.id.receivedProgramsFragment, null, provideNavOptions())
				currentFragmentTag = PROGRAMS_TAG
			}
			imageButtonIconProgramReceived.setOnClickListener {
				navController.navigate(R.id.receivedProgramsFragment, null, provideNavOptions())
				currentFragmentTag = PROGRAMS_TAG
			}
			textViewNavigationTrack.setOnClickListener {
				goTrack()
			}
			imageButtonIconTrack.setOnClickListener {
				goTrack()
			}
			textViewNavigationGPS.setOnClickListener {
				goGpsRunning()
			}
			imageButtonIconGPS.setOnClickListener {
				goGpsRunning()
			}
			feedbackContainer.setOnClickListener {
				onFeedback()
			}
			textViewNavigationAbout.setOnClickListener {
				navController.navigate(R.id.aboutFragment, null, provideNavOptions())
				currentFragmentTag = ABOUT_TAG
			}
			imageButtonIconAbout.setOnClickListener {
				navController.navigate(R.id.aboutFragment, null, provideNavOptions())
				currentFragmentTag = ABOUT_TAG
			}
			shareContainer.setOnClickListener {
				onShare()
			}
		}
	}

	private fun provideNavOptions() = NavOptions.Builder()
		.setLaunchSingleTop(true)
		.setPopUpTo(R.id.mainFragment, false)
		.build()

	companion object {
		const val HOME_TAG = "HomeFragment"
		const val PROGRAMS_TAG = "ProgramFragment"
		const val ABOUT_TAG = "AboutFragment"
		const val PROFILE_TAG = "ProfileFragment"

		const val LOGOUT_DIALOG_TAG = "NavigationDrawer.LogoutDialog"
		const val COMING_SOON_TAG = "NavigationDrawer.ComingSoonDialog"
		const val LANGUAGE_TAG = "NavigationDrawer.LanguageDialog"
	}
}