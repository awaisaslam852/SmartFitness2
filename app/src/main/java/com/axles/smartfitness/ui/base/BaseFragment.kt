package com.axles.smartfitness.ui.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.ui.activity.MainActivity
import com.phelat.navigationresult.BundleFragment

abstract class BaseFragment(@LayoutRes val layoutResId: Int) : BundleFragment() {
	protected val navController by lazy { findNavController() }

	protected fun openDrawer() {
		if (activity is MainActivity) {
			(activity as MainActivity).openDrawer()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		resolveArguments()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(layoutResId, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		init()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<ImageView>(R.id.topBarBackBtn)?.setOnClickListener {
			findNavController().popBackStack()
		}
	}

	override fun onResume() {
		super.onResume()
		update()
	}

	protected open fun resolveArguments() {}
	protected open fun init() {}
	protected open fun update() {}
	open fun onBackPressed() {}

	@Suppress("SameParameterValue")
	protected fun openPopUpFragment(fragment: Fragment, @IdRes container: Int) {
		childFragmentManager.beginTransaction()
			.add(container, fragment)
			.addToBackStack(null)
			.commit()
	}

	protected fun getCallerFragment(): Fragment? {
		val count = parentFragmentManager.backStackEntryCount
		if (count >= 2) {
			val id = requireFragmentManager().getBackStackEntryAt(count-2).id
			val result = requireFragmentManager().findFragmentById(id)
			return result
		}
		return null
	}
}