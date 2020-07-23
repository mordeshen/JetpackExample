package com.e.jetpackexample.repostiory.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.Response
import com.e.jetpackexample.ui.ResponseType
import com.e.jetpackexample.ui.auth.state.AuthViewState
import com.e.jetpackexample.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.e.jetpackexample.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
    @Inject
        constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password).switchMap { response ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (response) {
                        is GenericApiResponse.ApiSuccessResponse -> {
                            value = DataState.data(
                                data = AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk,
                                        response.body.token
                                    )
                                ),
                                response = null
                            )
                        }
                        is GenericApiResponse.ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )

                        }
                        is GenericApiResponse.ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email, username, password, confirmPassword)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is GenericApiResponse.ApiSuccessResponse -> {
                                value = DataState.data(
                                    AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is GenericApiResponse.ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )

                            }
                            is GenericApiResponse.ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }
}