package com.e.jetpackexample.di.auth

import com.e.jetpackexample.ui.auth.ForgotPasswordFragment
import com.e.jetpackexample.ui.auth.LauncherFragment
import com.e.jetpackexample.ui.auth.LoginFragment
import com.e.jetpackexample.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment
}