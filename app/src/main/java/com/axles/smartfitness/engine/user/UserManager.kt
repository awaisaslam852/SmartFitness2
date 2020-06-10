package com.axles.smartfitness.engine.user

import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Constants
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.network.enqueue
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.provider.RetrofitProvider
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import javax.crypto.SecretKey

object UserManager: ApiManager() {
	interface UserApi {
		@POST("/users")
		fun registerUser(@Body registerUserRequest: RegistrationRequest): Call<Token>

		@POST("/users/reset")
		fun resetPassword(@Body passwordRecoveryRequest: PasswordRecoveryRequest): Completable

		@GET("users/me")
		fun fetchUser(): Single<User>

		@POST("users/auth")
		fun loginViaEmail(@Body body: LoginViaEmailRequest, @Header("Accept-Language") language: String = LocalizationSettingsManager.apiHeader()): Call<Token>

		@POST("/users/auth/facebook")
		fun loginWithFacebook(@Query("token") token: String): Call<Token>

		@POST("/users/auth/google")
		fun loginWithGoogle(@Query("token") token: String): Call<Token>

		@POST("/users/auth/instagram")
		fun loginWithInstagram(@Query("code") token: String): Single<Token>

		@POST("/users/auth/twitter")
		fun loginWithTwitter(@Body twitterLoginRequest: TwitterLoginRequest): Call<Token>

		@POST("users/logout")
		fun logout(@Header("Authorization") token: String = currentToken()): Call<Void>
	}

	data class LoginViaEmailRequest(val username: String, val password: String)
	data class PasswordRecoveryRequest(val email: String)
	data class RegistrationRequest(var username: String, var email: String, var password: String)
	data class TwitterLoginRequest(val accessToken: String, val accessTokenSecret: String)

	private val api by lazy { RetrofitProvider.retrofit.create(UserApi::class.java) }

	private val currentToken = Token()

	fun currentToken() = currentToken.token()
	fun hasLoggedIn() = currentToken.token().isNotEmpty()

	fun login(username: String, password: String, completionListener: CompletionListener) {
		val request = api.loginViaEmail(LoginViaEmailRequest(username, password))
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when (error) {
					null -> {
						currentToken.token = response.body()!!.token
						when {
							!hasLoggedIn() -> runCompletionListener(completionListener, Helper.string(R.string.something_went_wrong))
							else -> runCompletionListener(completionListener, null)
						}
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun signUp(username: String, email: String, password: String, completionListener: CompletionListener) {
		val request = api.registerUser(RegistrationRequest(username, email, password))
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when (error) {
					null -> {
						currentToken.token = response.body()!!.token
						when {
							!hasLoggedIn() -> runCompletionListener(completionListener, Helper.string(R.string.something_went_wrong))
							else -> runCompletionListener(completionListener, null)
						}
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun loginWithFacebook(token: String, completionListener: CompletionListener) {
		val request = api.loginWithFacebook(token)
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when (error) {
					null -> {
						currentToken.token = response.body()!!.token
						when {
							!hasLoggedIn() -> runCompletionListener(completionListener, Helper.string(R.string.something_went_wrong))
							else -> runCompletionListener(completionListener, null)
						}
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun loginWithGoogle(token: String, completionListener: CompletionListener) {
		val request = api.loginWithGoogle(token)
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when (error) {
					null -> {
						currentToken.token = response.body()!!.token
						when {
							!hasLoggedIn() -> runCompletionListener(completionListener, Helper.string(R.string.something_went_wrong))
							else -> runCompletionListener(completionListener, null)
						}
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun loginWithTwitter(accessToken: String, secretKey: String, completionListener: CompletionListener) {
		val request = api.loginWithTwitter(TwitterLoginRequest(accessToken, secretKey))
		request.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				when (error) {
					null -> {
						currentToken.token = response.body()!!.token
						when {
							!hasLoggedIn() -> runCompletionListener(completionListener, Helper.string(R.string.something_went_wrong))
							else -> runCompletionListener(completionListener, null)
						}
					}
					else -> runCompletionListener(completionListener, error)
				}
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun logout() {
		val request = api.logout()
		request.enqueue {
			onResponse = {
				currentToken.reset()
			}
		}
	}
}