package com.e.jetpackexample

import com.e.jetpackexample.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {
    private val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

}