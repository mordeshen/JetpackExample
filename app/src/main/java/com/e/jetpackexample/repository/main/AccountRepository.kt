package com.e.jetpackexample.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.e.jetpackexample.api.GenericResponse
import com.e.jetpackexample.api.main.OpenApiMainService
import com.e.jetpackexample.models.AccountProperties
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.persistance.AccountPropertiesDao
import com.e.jetpackexample.repository.JobManager
import com.e.jetpackexample.repository.NetworkBoundResource
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.Response
import com.e.jetpackexample.ui.ResponseType
import com.e.jetpackexample.ui.main.account.state.AccountViewState
import com.e.jetpackexample.util.AbsentLiveData
import com.e.jetpackexample.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) : JobManager("AccountRepository") {
    private val TAG = "AccountRepository"

    @InternalCoroutinesApi
    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToInternet(),
                true,
                false,
                true
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"
                )
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(
                            DataState.data(
                                data = viewState,
                                response = null
                            )
                        )

                    }
                }
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }

                    }

            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username

                    )
                }
            }

        }.asLiveData()
    }

    @InternalCoroutinesApi
    fun saveAccountProperties(
        authToken: AuthToken,
        accountProperties: AccountProperties
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            isNetworkAvailable = sessionManager.isConnectedToInternet(),
            isNetworkRequest = true,
            shouldCancelIfNotInternet = true,
            shouldLoadFromCache = false
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                // TODO: 23/08/2020 update
                updateLocalDb(null)

                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.respose, ResponseType.Toast())
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            //not use in this case
            override suspend fun createCacheRequestAndReturn() {
            }

        }.asLiveData()
    }

    @InternalCoroutinesApi
    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false

        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {

                    //finish eith success response
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.respose, ResponseType.Toast())
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }

            //not Applicable in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            //not Applicable in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            //not Applicable in this case
            override suspend fun createCacheRequestAndReturn() {
            }

        }.asLiveData()
    }

}
