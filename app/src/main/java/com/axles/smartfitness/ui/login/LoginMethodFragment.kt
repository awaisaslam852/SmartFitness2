package com.axles.smartfitness.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.engine.network.session.SessionManagerFactory
import com.axles.smartfitness.ui.dialogs.ChooseLanguageDialog
import kotlinx.android.synthetic.main.login_method_fragment.*
import java.lang.IllegalStateException

class LoginMethodFragment : Fragment(){

    private lateinit var viewModel: LoginMethodViewModel
    private val navController: NavController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_method_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNewSession()

        setupViewModel()
        setupButtonListeners()
        setupLanguageDialog()
    }

    /**
     * Clear previous session
     */
    private fun setupNewSession() {
        logD("Setup new sessios")
        SessionManagerFactory.sessionManager.clearAccessToken()
    }

    /**
     * initialization of language selection dialog
     */
    private fun setupLanguageDialog() {
        textViewLanguage.setOnClickListener {
            val dialogFragment =
                ChooseLanguageDialog()
            dialogFragment.show(fragmentManager ?: throw IllegalStateException("Fragment manager not found"),
                DIALOG_TAG)
        }
    }

    /**
     * initialization of LoginMethodViewModel
     */
    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginMethodViewModel::class.java)
    }

    /**
     * initialization of buttons` onClickListener
     */
    private fun setupButtonListeners() {
        loginBtn.setOnClickListener {
            navController.navigate(R.id.loginMethodToLogIn)
        }

        buttonSignUp.setOnClickListener {
            navController.navigate(R.id.loginMethodToSignUp)
        }
    }

    companion object{
        private const val DIALOG_TAG = "LoginMethodFragment.LanguageSelectionDialog"
        fun newInstance() = LoginMethodFragment()
    }
}