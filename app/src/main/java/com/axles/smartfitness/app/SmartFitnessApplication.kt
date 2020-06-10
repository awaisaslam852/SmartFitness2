package com.axles.smartfitness.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.ui.login.LoginFragment
import com.axles.smartfitness.ui.resistance.ResistanceTrainingFragment
import com.facebook.FacebookSdk
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class SmartFitnessApplication : Application() {
	companion object {
		lateinit var instance: SmartFitnessApplication
	}

	var loginFragment: LoginFragment? = null
	var resistanceTrainingFragment: ResistanceTrainingFragment? = null

	override fun onCreate() {
		super.onCreate()
		FacebookSdk.sdkInitialize(this)
		val twitterConfig = TwitterConfig.Builder(this)
			.logger(DefaultLogger(Log.DEBUG))
			.twitterAuthConfig(TwitterAuthConfig(getString(R.string.twitter_consumer_key),
				getString(R.string.twitter_consumer_secret)))
			.debug(true)
			.build()

		Twitter.initialize(twitterConfig)
		instance = this
	}
}