package com.axles.smartfitness.ui.login

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.makeInvisible
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.livedata.ResponseData
import com.axles.smartfitness.ui.base.BaseFragment
import kotlinx.android.synthetic.main.forgot_password_fragment.*
import kotlinx.android.synthetic.main.toolbar_wave.*

class ForgotPasswordFragment : BaseFragment(R.layout.forgot_password_fragment){

	enum class ButtonState{
		SEND,
		DONE
	}

	var buttonState = ButtonState.SEND

	override fun init() {
//		viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

		setupButtonListeners()
		setupObserver()
	}

	private fun setupObserver() {
//		viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
//			when (it){
//				is ResponseData.SuccessResponse -> emailSent()
//				is ResponseData.ErrorResponse -> wrongEmail()
//			}
//		})
	}

	private fun wrongEmail() {
		buttonSend.isEnabled = true
		buttonState = ButtonState.SEND
		Toast.makeText(requireContext(),R.string.no_user_with_such_email, Toast.LENGTH_LONG).show()
	}

	/**
	 * change fragment state when email existence confirmed
	 */
	private fun emailSent() {
		imageButtonArrowBack.makeInvisible()
		imageViewArrowDone.makeInvisible()
		layoutForgotPasswordContainer.makeGone()
		successContainer.makeVisible()
		buttonSend.isEnabled = true
		buttonSend.text = getString(R.string.done)
		buttonState = ButtonState.DONE
	}

	/**
	 * initialization of buttons` listeners
	 */
	private fun setupButtonListeners() {
		imageButtonArrowBack.setOnClickListener {
			navController.popBackStack()
		}

		buttonSend.setOnClickListener {
			defaultContainer.makeGone()
			successContainer.makeVisible()
		}

		doneBtn.setOnClickListener {
			findNavController().navigateUp()
		}
	}

	private fun handleDone() {
		navController.popBackStack()
	}

	private fun handleSendEmail() {
//		viewModel.restorePassword(editTextEmail.text.toString())
//		buttonSend.isEnabled = false


	}



}