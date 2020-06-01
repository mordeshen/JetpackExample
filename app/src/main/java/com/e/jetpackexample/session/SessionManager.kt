package com.e.jetpackexample.session

import android.app.Application
import com.e.jetpackexample.persistance.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
){

}