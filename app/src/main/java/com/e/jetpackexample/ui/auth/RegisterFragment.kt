package com.e.jetpackexample.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.e.jetpackexample.R
import com.e.jetpackexample.ui.auth.state.AuthStateEvent
import com.e.jetpackexample.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "RegisterFragment: $viewModel")

        register_button.setOnClickListener {
            register()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let { loginFields ->
                loginFields.registration_email?.let { et_register_email.setText(it) }
                loginFields.registration_username?.let { et_register_username.setText(it) }
                loginFields.registration_password?.let { et_register_password.setText(it) }
                loginFields.registration_confirm_password?.let {
                    et_register_confirm_password.setText(
                        it
                    )
                }
            }
        })
    }

    fun register() {
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                et_register_email.text.toString(),
                et_register_username.text.toString(),
                et_register_password.text.toString(),
                et_register_confirm_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                et_register_email.text.toString(),
                et_register_username.text.toString(),
                et_register_password.text.toString(),
                et_register_confirm_password.text.toString()
            )
        )
    }
}
