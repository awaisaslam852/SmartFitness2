package com.axles.smartfitness.provider

import com.axles.smartfitness.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RetrofitProvider {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(OkHttpClientProvider.okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GSONConverterFactoryProvider.factory)
            .build()
    }
}