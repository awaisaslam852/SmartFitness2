package com.axles.smartfitness.engine.network

import android.os.Handler
import androidx.annotation.StringRes
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.engine.Helper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

open class ApiManager {
	interface CompletionListener {
		fun onCompleted(error: String?)
	}

	interface ResultCompletionListener {
		fun onCompleted(result: Any?, error: String?)
	}

	fun runCompletionListener(completionListener: CompletionListener?, @StringRes resourceId: Int) {
		runCompletionListener(completionListener, Helper.string(resourceId))
	}

	fun runCompletionListener(completionListener: CompletionListener?, exception: Exception) {
		runCompletionListener(completionListener, exception.localizedMessage)
	}

	fun runCompletionListener(completionListener: CompletionListener?, throwable: Throwable) {
		runCompletionListener(completionListener, throwable.localizedMessage)
	}

	fun runCompletionListener(completionListener: CompletionListener?, error: String?) {
		val context = SmartFitnessApplication.instance
		Handler(context.mainLooper).post {
			completionListener?.onCompleted(error)
		}
	}

	fun runResultCompletionListener(completionListener: ResultCompletionListener?, result: Any?, @StringRes resourceId: Int) {
		runResultCompletionListener(completionListener, result, Helper.string(resourceId))
	}

	fun runResultCompletionListener(completionListener: ResultCompletionListener?, result: Any?, exception: Exception) {
		runResultCompletionListener(completionListener, result, exception.localizedMessage)
	}

	fun runResultCompletionListener(completionListener: ResultCompletionListener?, result: Any?, error: String?) {
		val context = SmartFitnessApplication.instance
		Handler(context.mainLooper).post {
			completionListener?.onCompleted(result, error)
		}
	}

	class ErrorHandler<T>(val response: Response<T>) {
		fun parseError(): String? {
			try {
				if (response.errorBody() != null) {
					val jsonError = response.errorBody()!!.string()
					val json = JSONObject(jsonError)
					return json.getString("message")
				}

				if (response.body() == null) {
					val errorMessage = response.message()
					return if (errorMessage.isNullOrEmpty()) null else errorMessage
				}
			} catch (e: Exception) {
				e.printStackTrace()
				return e.localizedMessage
			}

			return null
		}
	}
}

fun<T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
	val callBackKt = CallBackKt<T>()
	callback.invoke(callBackKt)
	this.enqueue(callBackKt)
}

class CallBackKt<T>(
	var onResponse: ((Response<T>) -> Unit)? = null,
	var onFailure: ((t: Throwable?) -> Unit)? = null
): Callback<T> {
	override fun onFailure(call: Call<T>, t: Throwable) {
		onFailure?.invoke(t)
	}

	override fun onResponse(call: Call<T>, response: Response<T>) {
		onResponse?.invoke(response)
	}

}