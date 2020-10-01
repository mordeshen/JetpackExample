package com.e.jetpackexample.di.auth

import android.content.SharedPreferences
import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.repository.auth.AuthRepository
import com.e.jetpackexample.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }



    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        sharedPreferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ):AuthRepository{
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            editor
        )
    }
}