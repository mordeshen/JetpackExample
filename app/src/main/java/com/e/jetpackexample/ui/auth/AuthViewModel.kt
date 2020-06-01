package com.e.jetpackexample.ui.auth

import androidx.lifecycle.ViewModel
import com.e.jetpackexample.repostiory.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):ViewModel(){

}