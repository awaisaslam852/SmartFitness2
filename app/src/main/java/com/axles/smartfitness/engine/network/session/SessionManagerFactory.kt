package com.axles.smartfitness.engine.network.session

object SessionManagerFactory {
    val sessionManager: SessionManager by lazy {
		SessionManagerImpl()
    }
}