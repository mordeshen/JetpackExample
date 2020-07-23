package com.e.jetpackexample.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.e.jetpackexample.BaseActivity
import com.e.jetpackexample.R
import com.e.jetpackexample.ui.auth.AuthActivity

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        subscribeObservers()
    }

    fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeObservers: authToken: ${authToken}")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
                finish()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}