package com.e.jetpackexample.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.e.jetpackexample.BaseActivity
import com.e.jetpackexample.R
import com.e.jetpackexample.ui.ResponseType
import com.e.jetpackexample.ui.main.MainActivity
import com.e.jetpackexample.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    private val TAG = "AuthActivity"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            Log.d(TAG, "subscribeObservers: datastate1 ${dataState.data}")
            dataState.data?.let { data ->
                Log.d(TAG, "subscribeObservers: datastate2")
                data.data?.let { event ->
                    Log.d(TAG, "subscribeObservers: datastate3")
                    event.getContentIfNotHandled()?.let {
                        Log.d(TAG, "subscribeObservers: event")
                        it.authToken?.let {
                            Log.d(TAG, "subscribeObservers: dataState: ${it}")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when (it.responseType) {
                            is ResponseType.Dialog -> {
                                //inflate error dialog
                            }
                            is ResponseType.Toast -> {
                                //show toast
                            }
                            is ResponseType.None -> {
                                Log.e(TAG, "subscribeObservers: response: ${it.message}")
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer { it ->
            it.authToken?.let { authToken ->
                sessionManager.login(authToken)
            }
        })
        sessionManager.cachedToken.observe(this, Observer { dataState ->
            Log.d(TAG, "subscribeObservers: authToken ${dataState} ")
            dataState.let { authToken ->
                if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                    navMainActivity()
                }
            }
        })
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
