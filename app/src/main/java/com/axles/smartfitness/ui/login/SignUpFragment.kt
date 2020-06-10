package com.axles.smartfitness.ui.login

import android.app.Activity
import android.content.Intent
import android.util.Patterns
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.extensions.*
import com.axles.smartfitness.ui.base.BaseFragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.sign_up_fragment.*
import kotlinx.android.synthetic.main.toolbar_wave.*
import java.io.IOException

class SignUpFragment : BaseFragment(R.layout.sign_up_fragment) {

	companion object {
		private const val GOOGLE_SIGN_IN_REQUEST_CODE = 112
		private const val MIN_USERNAME_LENGTH = 3
		private const val MIN_PASSWORD_LENGTH = 6
		private const val DIALOG_TAG = "SignUpFragment.InstagramAuthenticationDialog"
	}

	private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
	private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
	private val twitterAuthClient = TwitterAuthClient()
	private val compositeDisposable = CompositeDisposable()

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		callbackManager.onActivityResult(requestCode, resultCode, data)
		twitterAuthClient.onActivityResult(requestCode, resultCode, data)
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			handleGoogleLoginResult(data)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		compositeDisposable.clear()
	}

	override fun init() {
		imageButtonArrowBack.setOnClickListener { navController.popBackStack() }
		signUpBtn.setOnClickListener { signUpWithEmail() }
		facebookBtn.setOnClickListener { signInWithFacebook() }
		googleBtn.setOnClickListener { signInWithGoogle() }
		instagramBtn.setOnClickListener { signInWithInstagram() }
		twitterBtn.setOnClickListener { signInWithTwitter() }
		textViewTermsOfService.setOnClickListener { onTermsOfService() }
		textViewPrivacyPolicy.setOnClickListener { onPrivacyPolicy() }
	}

	private fun signUpFieldsAreValid() = isUsernameValid() && isEmailValid() && isPasswordValid()

	private fun isPasswordValid(): Boolean {
		val password = editTextPassword.text.toString()
		when {
			password.length < MIN_PASSWORD_LENGTH -> {
				Helper.showErrorAlert(childFragmentManager, R.string.password_must_be_at_least_6_characters)
				imageViewErrorPassword.makeVisible()
				imageViewErrorPassword.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.password_must_be_at_least_6_characters)
				}
				return false
			}
			password.contains(Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) -> return true
			else -> {

				Helper.showErrorAlert(childFragmentManager, R.string.password_must_be_at_least_6_characters)
				imageViewErrorPassword.makeVisible()
				imageViewErrorPassword.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.password_must_be_at_least_6_characters)
				}
				return false
			}
		}
	}

	private fun isUsernameValid(): Boolean {
		val username = editTextUsername.text.toString()
		when {
			username.length < MIN_USERNAME_LENGTH -> {
				Helper.showErrorAlert(childFragmentManager, R.string.username_must_be_3_to_14_characters)
				imageViewErrorUsername.makeVisible()
				imageViewErrorUsername.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.username_must_be_3_to_14_characters)
				}
				return false
			}
			username.contains(" ") -> {
				Helper.showErrorAlert(childFragmentManager, R.string.please_enter_only_letters_and_numbers_without_space)
				imageViewErrorUsername.makeVisible()
				imageViewErrorUsername.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.please_enter_only_letters_and_numbers_without_space)
				}
				return false
			}
			else -> return true
		}
	}

	private fun isEmailValid(): Boolean {
		val email = editTextEmail.text.toString()
		when {
			email.isBlank() -> {
				Helper.showErrorAlert(childFragmentManager, R.string.invalid_email_address)
				imageViewErrorEmail.makeVisible()
				imageViewErrorEmail.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.invalid_email_address)
				}

				return false
			}
			Patterns.EMAIL_ADDRESS.matcher(email).matches() -> return true
			else -> {
				Helper.showErrorAlert(childFragmentManager, R.string.invalid_email_address)
				imageViewErrorEmail.makeVisible()
				imageViewErrorEmail.setOnClickListener {
					Helper.showErrorAlert(childFragmentManager, R.string.invalid_email_address)
				}
				return false
			}
		}
	}

	private fun goToApp() {
		navController.navigate(R.id.signUpToHome)
	}

	private fun clearAllErrors() {
		imageViewErrorUsername.makeGone()
		imageViewErrorEmail.makeGone()
		imageViewErrorPassword.makeGone()
	}

	private fun signUpWithEmail() {
		clearAllErrors()
		if (!signUpFieldsAreValid()) {
			clearAllErrors()
			return
		}

		val username = editTextUsername.text.toString()
		val password = editTextPassword.text.toString()
		val email = editTextEmail.text.toString()

		Helper.showLoading(childFragmentManager)
		UserManager.signUp(username, email, password, object : ApiManager.CompletionListener {
			override fun onCompleted(error: String?) {
				Helper.hideLoading()
				if (error != null) {
					Helper.showErrorAlert(childFragmentManager, error)
					return
				}

				goToApp()
			}
		})
	}

	private fun signInWithFacebook() {
		Helper.showLoading(childFragmentManager)
		LoginManager.getInstance().registerCallback(callbackManager, object :
			FacebookCallback<LoginResult> {
			override fun onSuccess(result: LoginResult) {
				Helper.hideLoading()
				val token = result.accessToken
				handleFacebookAccessToken(token.token)
			}

			override fun onCancel() {
				Helper.hideLoading()
				logD("onCancel Facebook")
			}

			override fun onError(error: FacebookException?) {
				Helper.hideLoading()
				Helper.showErrorAlert(childFragmentManager, error?.localizedMessage)
				error?.printStackTrace()
			}
		})

		val facebookPermissions = "public_profile"
		LoginManager.getInstance().logOut()
		LoginManager.getInstance().logInWithReadPermissions(this, listOf(facebookPermissions))
	}

	private fun handleFacebookAccessToken(accessToken: String) {
		Helper.showLoading(childFragmentManager)
		val credential = FacebookAuthProvider.getCredential(accessToken)
		firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
			if (!task.isSuccessful) {
				Helper.hideLoading()
				Helper.showErrorAlert(childFragmentManager, task.exception?.localizedMessage)
				return@addOnCompleteListener
			}

			UserManager.loginWithFacebook(accessToken, object : ApiManager.CompletionListener {
				override fun onCompleted(error: String?) {
					Helper.hideLoading()
					if (error != null) {
						Helper.showErrorAlert(childFragmentManager, error)
						return
					}

					goToApp()
				}
			})
		}
	}

	private fun signInWithInstagram() {
		val dialog = InstagramAuthenticationDialog(::instagramTokenReceivedListener)
		dialog.isCancelable = true
		dialog.show(fragmentManager ?: throw IllegalStateException("Fragment manager not found"),
			DIALOG_TAG)
	}

	private fun instagramTokenReceivedListener(token: String?){
		logD("instagram token received listener $token")
		token?.let {
//            loginViewModel.loginWithInstagram(it)
		}
	}

	private fun signInWithGoogle() {
		val webClientId = Helper.string(R.string.web_client_id)

		val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestServerAuthCode(webClientId)
			.requestEmail()
			.requestScopes(Scope(Scopes.PROFILE), Scope(Scopes.EMAIL))
			.build()

		val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
		googleSignInClient.signOut().addOnCompleteListener {
			val intent = googleSignInClient!!.signInIntent
			startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
		}
	}

	private fun handleGoogleLoginResult(data: Intent?) {
		if (data == null) { return }
		val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
		val googleSignInAccount = task.getResult(ApiException::class.java)

		compositeDisposable.add(getGoogleToken(googleSignInAccount)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe({ token ->
				handleGoogleToken(token)
			}, { throwable ->
				Helper.showErrorAlert(childFragmentManager, throwable.localizedMessage)
			}))
	}

	private fun getGoogleToken(googleSignInAcc: GoogleSignInAccount?): Single<String> =
		Single.create {
			var token: String? = null
			try {
				val scopes = "oauth2:profile email"
				token = GoogleAuthUtil.getToken(this.requireContext(), googleSignInAcc?.account, scopes)
			} catch (gaex: GoogleAuthException) {
				logD("auth ex in getGoogleToken: ${gaex.localizedMessage}")
				it.onError(gaex)
			} catch (ex: IOException) {
				logD("io ex in getGoogleToken: ${ex.localizedMessage}")
				it.onError(ex)
			}

			if (token != null) {
				it.onSuccess(token)
			} else {
				it.onError(NullPointerException("GoogleToken is Null"))
			}
		}

	private fun handleGoogleToken(token: String) {
		Helper.showLoading(childFragmentManager)
		UserManager.loginWithGoogle(token, object : ApiManager.CompletionListener {
			override fun onCompleted(error: String?) {
				Helper.hideLoading()
				if (error != null) {
					Helper.showErrorAlert(childFragmentManager, error)
					return
				}
				goToApp()
			}
		})
	}

	private fun signInWithTwitter() {
		twitterAuthClient.authorize(this.requireActivity(), object : Callback<TwitterSession>(){
			override fun success(result: Result<TwitterSession>) {
				val accessToken = result.data?.authToken?.token!!
				val secretKey = result.data?.authToken?.secret!!
				handleTwitterLogin(accessToken, secretKey)
			}

			override fun failure(exception: TwitterException?) {
				exception?.printStackTrace()
				Helper.showErrorAlert(childFragmentManager, exception?.localizedMessage)
			}
		})
	}

	private fun handleTwitterLogin(accessToken: String, secretKey: String) {
		Helper.showLoading(childFragmentManager)
		UserManager.loginWithTwitter(accessToken, secretKey, object : ApiManager.CompletionListener {
			override fun onCompleted(error: String?) {
				Helper.hideLoading()
				if (error != null) {
					Helper.showErrorAlert(childFragmentManager, error)
					return
				}

				goToApp()
			}
		})
	}

	private fun onTermsOfService() {
		val action = LoginFragmentDirections.toWebView(Constants.termsOfServiceUrl)
		findNavController().navigate(action)
	}

	private fun onPrivacyPolicy() {
		val action = LoginFragmentDirections.toWebView(Constants.privacyPolicyUrl)
		findNavController().navigate(action)
	}
}