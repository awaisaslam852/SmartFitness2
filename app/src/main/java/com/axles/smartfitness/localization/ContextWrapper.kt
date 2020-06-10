package com.axles.smartfitness.localization

import android.content.Context
import android.os.Build
import android.os.LocaleList

class ContextWrapper(base: Context): android.content.ContextWrapper(base) {
	companion object {
		fun wrap(context: Context): ContextWrapper {
			val res = context.resources
			val configuration = res.configuration

			val locale = LocalizationSettingsManager.locale()
			configuration.setLocale(locale)
			configuration.setLayoutDirection(locale)
			context.resources.updateConfiguration(configuration, res.displayMetrics)
			return ContextWrapper(context.createConfigurationContext(configuration))

//			when {
//				Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
//					configuration.setLocale(locale)
//					val localeList = LocaleList(locale)
//					LocaleList.setDefault(localeList)
//					configuration.setLocales(localeList)
//					return ContextWrapper(context.createConfigurationContext(configuration))
//				}
//				Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
//					configuration.setLocale(locale)
//					return ContextWrapper(context.createConfigurationContext(configuration))
//				}
//				else -> {
//					configuration.locale = locale
//					res.updateConfiguration(configuration, res.displayMetrics)
//				}
//			}
//
//			return ContextWrapper(context)
		}
	}
}