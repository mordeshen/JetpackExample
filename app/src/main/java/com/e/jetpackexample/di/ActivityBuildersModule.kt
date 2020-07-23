package com.e.jetpackexample.di

import com.e.jetpackexample.di.auth.AuthFragmentBuildersModule
import com.e.jetpackexample.di.auth.AuthModule
import com.e.jetpackexample.di.auth.AuthScope
import com.e.jetpackexample.di.auth.AuthViewModelModule
import com.e.jetpackexample.ui.auth.AuthActivity
import com.e.jetpackexample.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}