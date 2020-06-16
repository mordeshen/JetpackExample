package com.e.jetpackexample.ui.auth.state

sealed class AuthStateEvent {

    //that is the 'requests' from auth viewModel

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ): AuthStateEvent()

    data class RegisterAttemptEvent(
        val email: String,
        val username:String,
        val password: String,
        val confirm_password:String
    ):AuthStateEvent()

     class CheckPreviousAuthEvent(): AuthStateEvent()
}