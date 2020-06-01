package com.e.jetpackexample.di.auth

import androidx.lifecycle.ViewModel
import com.e.jetpackexample.di.ViewModelKey
import com.e.jetpackexample.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel):ViewModel
}