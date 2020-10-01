package com.e.jetpackexample.ui.main.account

import androidx.lifecycle.LiveData
import com.e.jetpackexample.models.AccountProperties
import com.e.jetpackexample.repository.main.AccountRepository
import com.e.jetpackexample.session.SessionManager
import com.e.jetpackexample.ui.BaseViewModel
import com.e.jetpackexample.ui.DataState
import com.e.jetpackexample.ui.Loading
import com.e.jetpackexample.ui.main.account.state.AccountStateEvent
import com.e.jetpackexample.ui.main.account.state.AccountStateEvent.*
import com.e.jetpackexample.ui.main.account.state.AccountViewState
import com.e.jetpackexample.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {
    @InternalCoroutinesApi
    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }
            is UpdateAccountProperties -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken,
                            AccountProperties(
                                pk,
                                stateEvent.email,
                                stateEvent.username
                            )
                        )
                    }
                } ?: AbsentLiveData.create()
            }
            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmPassword
                    )
                } ?: AbsentLiveData.create()
            }
            is None -> {
                return object : LiveData<DataState<AccountViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            error = null,
                            loading = Loading(false),
                            data = null
                        )
                    }
                }
            }

        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }

    fun cancelActiveJobs() {
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    //if vm cleared
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun handlePendingData() {
        setStateEvent(AccountStateEvent.None())
    }
}