package com.e.jetpackexample.repostiory.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.api.auth.network_responses.LoginResponse
import com.e.jetpackexample.api.auth.network_responses.RegistrationResponse
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.repostiory.NetworkBoundResource
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.Response
import com.e.jetpackexample.ui.ResponseType
import com.e.jetpackexample.ui.auth.state.AuthViewState
import com.e.jetpackexample.ui.auth.state.LoginFields
import com.e.jetpackexample.ui.auth.state.RegistrationFields
import com.e.jetpackexample.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.e.jetpackexample.util.GenericApiResponse
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
    @Inject
        constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    private val TAG = "AuthRepository"
    private var repositoryJob: Job? = null

    @OptIn(InternalCoroutinesApi::class)
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldsError = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsError.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldsError, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //handle 200  response if the credentials isnt correct
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }


    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {

        Log.d(TAG, "returnErrorResponse: $errorMessage")
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessage,
                        responseType
                    )
                )

            }
        }

    }

    fun cancelActiveJobs() {
        Log.d(TAG, "cancelActiveJobs:  cancelling on going jobs..")
        repositoryJob?.cancel()
    }

//    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
//        return openApiAuthService.login(email, password).switchMap { response ->
//            object : LiveData<DataState<AuthViewState>>() {
//                override fun onActive() {
//                    super.onActive()
//                    when (response) {
//                        is GenericApiResponse.ApiSuccessResponse -> {
//                            value = DataState.data(
//                                data = AuthViewState(
//                                    authToken = AuthToken(
//                                        response.body.pk,
//                                        response.body.token
//                                    )
//                                ),
//                                response = null
//                            )
//                        }
//                        is GenericApiResponse.ApiErrorResponse -> {
//                            value = DataState.error(
//                                response = Response(
//                                    message = response.errorMessage,
//                                    responseType = ResponseType.Dialog()
//                                )
//                            )
//
//                        }
//                        is GenericApiResponse.ApiEmptyResponse -> {
//                            value = DataState.error(
//                                response = Response(
//                                    message = ERROR_UNKNOWN,
//                                    responseType = ResponseType.Dialog()
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }

    @InternalCoroutinesApi
    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()
        if (!registrationFieldsErrors.equals(RegistrationFields.RegistrationErrors.none())) {
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }
}