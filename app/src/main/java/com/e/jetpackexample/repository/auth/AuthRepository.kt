package com.e.jetpackexample.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.e.jetpackexample.api.auth.OpenApiAuthService
import com.e.jetpackexample.api.auth.network_responses.LoginResponse
import com.e.jetpackexample.api.auth.network_responses.RegistrationResponse
import com.e.jetpackexample.models.AccountProperties
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.persistance.AuthTokenDao
import com.e.jetpackexample.repository.JobManager
import com.e.jetpackexample.repository.NetworkBoundResource
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.Response
import com.e.jetpackexample.ui.ResponseType
import com.e.jetpackexample.ui.auth.state.AuthViewState
import com.e.jetpackexample.ui.auth.state.LoginFields
import com.e.jetpackexample.ui.auth.state.RegistrationFields
import com.e.jetpackexample.util.AbsentLiveData
import com.e.jetpackexample.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.e.jetpackexample.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.e.jetpackexample.util.GenericApiResponse
import com.e.jetpackexample.util.PreferenceKeys
import com.e.jetpackexample.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) : JobManager("AuthRepository") {
    private val TAG = "AuthRepository"

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {

        Log.d(TAG, "attemptLogin: ")

        val loginFieldsError = LoginFields(email, password).isValidForLogin()

        if (!loginFieldsError.equals(LoginFields.LoginError.none())) {

            return returnErrorResponse(loginFieldsError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                //handle 200  response if the credentials isnt correct
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //insert if doesnt exist
                // b/c doreign key relationship with AuthToken table
                accountPropertiesDao.insertAndIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1. i do care b/c here i insert the value
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                Log.d(TAG, "createCall:  attempt to login")
                return openApiAuthService.login(email, password)
            }


            override fun setJob(job: Job) {
                addJob("attemptLogin", job)
            }

            //not use this case
            override suspend fun createCacheRequestAndReturn() {
            }

            //not use this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not use this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }


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

        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: ${response}")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //insert if doesnt exist
                // b/c doreign key relationship with AuthToken table
                accountPropertiesDao.insertAndIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1. i do care b/c here i insert the value
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }
                saveAuthenticatedUserToPrefs(email)

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
                addJob("attemptRegistration", job)
            }

            //not use this case
            override suspend fun createCacheRequestAndReturn() {
            }

            //not use this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not use this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: no previously authenticated user found")
            return returnNoTokenFound()
        }
        return object : NetworkBoundResource<Void, Any, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            false,
            false,
            false
        ) {
            //not use in this case
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<Void>) {
            }

            //not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                addJob("checkPreviousAuthUser", job)
            }

            //not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            //not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(
                        TAG,
                        "createCacheRequestAndReturn: searching for token: $accountProperties"
                    )
                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found")
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
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

    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }
}