package com.axles.smartfitness.engine.user

import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.extensions.logD
import com.google.gson.annotations.SerializedName

class Token {
	@SerializedName("token") var token: String = ""
		set(value) {field = value
			logD(token)
			save()
		}

	init {
		token = Helper.getSharedPreferences(Constants.User.Token, token)
	}

	fun token() = token

	private fun save() {
		Helper.putSharedPreferences(Constants.User.Token, token)
	}

	fun reset() {
		token = ""
	}
}