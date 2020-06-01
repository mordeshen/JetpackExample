package com.e.jetpackexample.repostiory.auth

import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.session.SessionManager
import javax.inject.Inject

class AuthRepository
    @Inject
        constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
){

}