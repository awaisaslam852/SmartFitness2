package com.axles.smartfitness.engine.network.session

interface SessionManager {

    fun saveAccessToken(token : String?)
    fun getAccessToken(): String?
    fun clearAccessToken()
    fun isLoggedIn(): Boolean

}