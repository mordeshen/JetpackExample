package com.e.jetpackexample.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun hideSoftKeyboard()
}