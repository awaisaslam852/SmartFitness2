package com.axles.smartfitness.engine.network.session

import android.content.Context
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.extensions.logD

class SessionManagerImpl: SessionManager {

	private val context = SmartFitnessApplication.instance.applicationContext
	private val preferences =
		context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

	override fun saveAccessToken(token: String?) {
		with(preferences.edit()) {
			putString(KEY_TOKEN, token)
			apply()
		}
	}

	override fun getAccessToken(): String? {
		return preferences.getString(KEY_TOKEN, null).also {
			logD("access token: $it")
		}
	}

	override fun clearAccessToken() {
		saveAccessToken(null)
	}

	override fun isLoggedIn(): Boolean {
		return getAccessToken() != null
	}


	companion object {
		const val SHARED_PREFERENCES_NAME = "com.axles.smartfitness"
		const val KEY_TOKEN = "com.axles.smartfitness.KEY_TOKEN"
	}
}