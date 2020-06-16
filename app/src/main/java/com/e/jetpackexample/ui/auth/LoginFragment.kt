package com.e.jetpackexample.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.e.jetpackexample.R
import com.e.jetpackexample.ui.auth.state.LoginFields
import com.e.jetpackexample.util.GenericApiResponse
import com.e.jetpackexample.util.GenericApiResponse.*
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment $viewModel")

        subscribeObservers()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let {loginFields ->
                loginFields.login_email?.let { et_login_mail.setText(it) }
                loginFields.login_password?.let { et_login_password.setText(it) }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //in order to save the fields in the registration fields
        viewModel.setLoginFields(
            LoginFields(
                et_login_mail.text.toString(),
                et_login_password.text.toString()
            )
        )
    }
}
