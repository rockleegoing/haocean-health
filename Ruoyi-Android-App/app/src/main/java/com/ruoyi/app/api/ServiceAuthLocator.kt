package com.ruoyi.app.api

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.ruoyi.app.api.repository.AuthRepoInterface
import com.ruoyi.app.api.repository.AuthRepository


object ServiceAuthLocator {

    private val lock = Any()

    @Volatile
    var authRepository: AuthRepoInterface? = null
        @VisibleForTesting set

    fun provideAuthRepository(context: Context): AuthRepoInterface {
        synchronized(this) {
            return authRepository ?: createAuthRepository(context)
        }
    }

    private fun createAuthRepository(context: Context): AuthRepoInterface {
        val newRepo = AuthRepository(context)
        authRepository = newRepo
        return newRepo
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            authRepository = null
        }
    }
}
