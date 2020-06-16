package com.e.jetpackexample.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.e.jetpackexample.api.auth.network_responses.LoginResponse
import com.e.jetpackexample.api.auth.network_responses.RegistrationResponse
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.repostiory.auth.AuthRepository
import com.e.jetpackexample.ui.BaseViewModel
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.auth.state.AuthStateEvent
import com.e.jetpackexample.ui.auth.state.AuthViewState
import com.e.jetpackexample.ui.auth.state.LoginFields
import com.e.jetpackexample.ui.auth.state.RegistrationFields
import com.e.jetpackexample.util.AbsentLiveData
import com.e.jetpackexample.util.GenericApiResponse
import javax.inject.Inject
import javax.inject.Singleton

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent,AuthViewState>(){
    // every viewModel can be kind of the same, handle the stateEvent, and set the situations
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            is AuthStateEvent.LoginAttemptEvent -> {
                return AbsentLiveData.create()
            }

            is AuthStateEvent.RegisterAttemptEvent -> {
                return AbsentLiveData.create()
            }

            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken:AuthToken){
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

}