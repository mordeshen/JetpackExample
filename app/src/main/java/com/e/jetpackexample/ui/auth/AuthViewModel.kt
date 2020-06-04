package com.e.jetpackexample.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.e.jetpackexample.api.auth.network_responses.LoginResponse
import com.e.jetpackexample.api.auth.network_responses.RegistrationResponse
import com.e.jetpackexample.repostiory.auth.AuthRepository
import com.e.jetpackexample.util.GenericApiResponse
import javax.inject.Inject
import javax.inject.Singleton

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):ViewModel(){

    fun testLogin():LiveData<GenericApiResponse<LoginResponse>>{
        return authRepository.testLoginRequest(
            "mordechay.shenvald@gmail.com",
            "Whatilove613"
        )
    }

    fun testRegister():LiveData<GenericApiResponse<RegistrationResponse>>{
        return authRepository.testRegistrationRequest(
            "mor.989.shen@gmail.com",
            "mordechay",
            "1234mordechay",
            "1234mordechay"
        )
    }

}