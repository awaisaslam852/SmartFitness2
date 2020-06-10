package com.axles.smartfitness.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.ContextThemeWrapper
import com.axles.smartfitness.app.SmartFitnessApplication
import java.util.*


object LocalizationSettingsManager {

	private const val SHARED_PREFERENCES = "SmartFitness.LocalizationSharedPreferences"
	private const val KEY_LOCALE = "KEY_LOCALE"
	private var locale: Locale = Locale.getDefault()
		set(value) {field = value
			applyLocal()
		}

	init {
		load()
	}

	private fun load() {
		val context = SmartFitnessApplication.instance
		val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
		val savedLanguage = sharedPreferences.getString(KEY_LOCALE, null)
		if (savedLanguage.isNullOrBlank()) {
			locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				context.resources.configuration.locales.get(0)
			} else {
				context.resources.configuration.locale;
			}
			return
		}
		locale = Locale(savedLanguage)
	}

	private fun save() {
		val context = SmartFitnessApplication.instance
		val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
		sharedPreferences.edit().putString(KEY_LOCALE, locale.language).apply()
	}

	private fun applyLocal() {
		Locale.setDefault(locale)
		save()
	}

	private fun setLocale(language: String) {
		locale = Locale(language)
	}

	fun setEn() {
		setLocale(Language.ENGLISH.locale)
	}

	fun setIw() {
		setLocale(Language.HEBREW.locale)
	}

	fun locale() = locale

	fun isIw() = locale.language == Locale("iw").language

	fun apiHeader() = if (isIw()) "iw" else "us"

	fun updateConfiguration(wrapper: ContextThemeWrapper) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			val configuration = Configuration(wrapper.resources.configuration)
			configuration.setLocale(locale)
			wrapper.applyOverrideConfiguration(configuration)
		}
	}

	fun updateConfiguration(context: Context?, configuration: Configuration?) {
		if (context == null) { return }
		if (configuration == null) { return }
		val config = Configuration(configuration)
		config.setLocale(locale)
		context.resources.updateConfiguration(config, context.resources.displayMetrics)
	}
}