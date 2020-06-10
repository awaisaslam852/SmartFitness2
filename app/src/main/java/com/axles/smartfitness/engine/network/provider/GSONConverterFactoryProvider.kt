package com.axles.smartfitness.provider

import retrofit2.converter.gson.GsonConverterFactory

object GSONConverterFactoryProvider {

    val factory : GsonConverterFactory by lazy {
        GsonConverterFactory.create()
    }

}