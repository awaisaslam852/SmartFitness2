package com.axles.smartfitness.ui.login
import android.app.Activity
import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.BuildConfig
import com.axles.smartfitness.R
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
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
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.toolbar_wave.*
import java.io.IOException
class LoginFragment: BaseFragment(R.layout.login_fragment) {

	companion object {
		private const val GOOGLE_SIGN_IN_REQUEST_CODE = 111
	}

	private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

	private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }

	private val compositeDisposable = CompositeDisposable()
	private val twitterAuthClient = TwitterAuthClient()

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		twitterAuthClient.onActivityResult(requestCode, resultCode, data)
		callbackManager.onActivityResult(requestCode, resultCode, data)

		if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			handleGoogleLoginResult(data)
		}
	}

	override fun onResume() {
		super.onResume()
		SmartFitnessApplication.instance.loginFragment = this
	}

	override fun onStop() {
		super.onStop()
		SmartFitnessApplication.instance.loginFragment = null
	}

	override fun onDestroy() {
		super.onDestroy()
		compositeDisposable.clear()
	}

	override fun init() {
		imageButtonArrowBack.setOnClickListener { navController.popBackStack() }
		forgotPasswordBtn.setOnClickListener { onForgotPassword() }
		loginBtn.setOnClickListener { login() }
		facebookBtn.setOnClickListener { loginWithFacebook() }
		googleBtn.setOnClickListener { loginWithGoogle() }
		twitterBtn.setOnClickListener { loginWithTwitter() }
		instagramBtn.setOnClickListener { loginWithInstagram() }
		textViewTermsOfService.setOnClickListener { onTermsOfService() }
		textViewPrivacyPolicy.setOnClickListener { onPrivacyPolicy() }

		if (BuildConfig.DEBUG) {
			editTextUsername.setText("sss")
			editTextPassword.setText("123456a")
		}
	}

	override fun update() {

	}

	private fun login() {
		clearErrors()
		if (!credentialsAreValid()) return

		val username = editTextUsername.text.toString()
		val password = editTextPassword.text.toString()

		Helper.showLoading(childFragmentManager)
		UserManager.login(username, password, object: ApiManager.CompletionListener {
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

	private fun goToApp() {
		navController.navigate(R.id.loginToMain)
	}

	private fun credentialsAreValid() = usernameIsValid() && passwordIsValid()
	private fun passwordIsValid(): Boolean {
		val password = editTextPassword.text.toString()
		if (password.length < 6) {
			imageViewErrorPassword.makeVisible()
			imageViewErrorPassword.setOnClickListener {
				Helper.showErrorAlert(childFragmentManager, R.string.password_must_be_at_least_6_characters)
			}
			return false
		}

		return true
	}

	private fun usernameIsValid() :Boolean {
		if (editTextUsername.length() > 2) { return true }

		imageViewErrorUsername.makeVisible()
		imageViewErrorUsername.setOnClickListener {
			Helper.showErrorAlert(childFragmentManager, R.string.invalid_email_address)
		}
		return false
	}

	private fun clearErrors() {
		imageViewErrorUsername.makeGone()
		imageViewErrorPassword.makeGone()
	}

	private fun loginWithFacebook() {
		Helper.showLoading(childFragmentManager)
		LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
			override fun onSuccess(loginResult: LoginResult) {
				Helper.hideLoading()
				val accessToken = loginResult.accessToken
				handleFacebookAccessToken(accessToken.token)
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

	private fun loginWithInstagram() {
		val dialog = InstagramAuthenticationDialog(::instagramCodeReceivedListener)
		dialog.isCancelable = true
		dialog.show(childFragmentManager, "LoginFragment.InstagramAuthenticationDialog")
	}

	private fun instagramCodeReceivedListener(code: String?){
		logD("instagram token received listener $code")
		code?.let {
//			loginViewModel.loginWithInstagram(it)
		}
	}

	private fun loginWithGoogle() {
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
			.subscribe({token ->
				handleGoogleToken(token)
			}, { throwable ->
				throwable.printStackTrace()
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

	private fun loginWithTwitter() {
		Helper.showLoading(childFragmentManager)
		twitterAuthClient.authorize(requireActivity(), object : Callback<TwitterSession>() {
			override fun success(result: Result<TwitterSession>) {
				val accessToken = result.data?.authToken?.token!!
				val secretKey = result.data?.authToken?.secret!!
				handleTwitterLogin(accessToken, secretKey)
			}

			override fun failure(exception: TwitterException?) {
				Helper.hideLoading()
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

	private fun onForgotPassword() {
		navController.navigate(R.id.loginToForgotPassword)
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