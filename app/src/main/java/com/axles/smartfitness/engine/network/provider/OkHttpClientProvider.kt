package com.axles.smartfitness.provider

import com.axles.smartfitness.network.interceptor.AuthenticationInterceptor
import com.axles.smartfitness.engine.network.session.SessionManagerFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpClientProvider {


    private const val TIMEOUT = 30L

    private val authenticationInterceptor: AuthenticationInterceptor by lazy {
        AuthenticationInterceptor(SessionManagerFactory.sessionManager)
    }

    private val httpLoggingInterceptor: HttpLoggingInterceptor
        get() {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return logInterceptor
        }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authenticationInterceptor)
            .build()
    }


}