package com.e.jetpackexample.repostiory.auth

import androidx.lifecycle.LiveData
import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.api.auth.network_responses.LoginResponse
import com.e.jetpackexample.api.auth.network_responses.RegistrationResponse
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
    @Inject
        constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
){

    fun testLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email,password)
    }

    fun testRegistrationRequest(email: String,
                                username:String,
                                password: String,
                                confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(email,username,password,confirmPassword)
    }
}