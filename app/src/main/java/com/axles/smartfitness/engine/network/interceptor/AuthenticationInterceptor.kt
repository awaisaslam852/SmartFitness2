package com.axles.smartfitness.network.interceptor

import com.axles.smartfitness.engine.network.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor (private val sessionManager: SessionManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getAccessToken()

        val original = chain.request()
        if (token == null) {
            return chain.proceed(original)
        }

        val requestBuilder = original.newBuilder()
            .header(AUTHORIZATION_HEADER, token)
            .method(original.method, original.body)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

    private companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}