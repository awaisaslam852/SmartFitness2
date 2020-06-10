package com.axles.smartfitness.engine.program

import com.axles.smartfitness.engine.network.ApiManager
import com.axles.smartfitness.engine.network.enqueue
import com.axles.smartfitness.engine.network.response.program.ApiPrograms
import com.axles.smartfitness.engine.user.UserManager
import com.axles.smartfitness.localization.LocalizationSettingsManager
import com.axles.smartfitness.provider.RetrofitProvider
import retrofit2.Call
import retrofit2.http.*

object ProgramManager: ApiManager() {
	private interface ProgramApi {
		@GET("programs")
		fun allPrograms(@Header("Authorization") token: String = UserManager.currentToken(),
						@Header("Accept-Language") language: String = LocalizationSettingsManager.apiHeader()): Call<ApiPrograms>

		@DELETE("programs/{id}")
		fun deleteProgram(@Path("id") id: Int, @Header("Authorization") token: String = UserManager.currentToken()): Call<Void>
	}

	private val api by lazy { RetrofitProvider.retrofit.create(ProgramApi::class.java) }

	val programs: MutableList<Program> = mutableListOf()

	fun isSavedProgram(program: Program): Boolean {
		return programs.any { it.id == program.id }
	}

	fun program(index: Int): Program? {
		return if (index >= programs.size) null else program(index)
	}

	fun save(program: Program) {
		remove(program)
		programs.add(program)
	}

	fun remove(program: Program) {
		val result = programs.firstOrNull { it.id == program.id } ?: return
		programs.remove(result)
	}

	fun getPrograms(completionListener: CompletionListener) {
		programs.clear()
		val call = api.allPrograms()
		call.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				if (error == null) {
					programs.addAll(response.body()!!.toPrograms())
				}
				runCompletionListener(completionListener, error)
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}

	fun deleteProgram(program: Program, completionListener: CompletionListener) {
		val call = api.deleteProgram(program.id)
		call.enqueue {
			onResponse = { response ->
				val error = ErrorHandler(response).parseError()
				if (error == null) { getPrograms(completionListener) }
				runCompletionListener(completionListener, error)
			}
			onFailure = {
				runCompletionListener(completionListener, it?.localizedMessage)
			}
		}
	}
}