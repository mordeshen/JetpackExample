package com.e.jetpackexample.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.persistance.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
    private val TAG = "SessionManager"

    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun logout() {
        Log.d(TAG, "logout...")
        GlobalScope.launch(IO) {
            var errorMessage: String? = null

            try {
                cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException) {
                Log.e(TAG, "logout:  ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: ${errorMessage}")
                }
                Log.d(TAG, "logout: finally..")
                setValue(null)
            }
        }
    }

    fun setValue(newValue: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }

    fun isConnectedToInternet(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.let {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                capabilities?.let {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return true
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            return true
                        }
                        else -> return true
                    }
                }
            } else {

                try {
                    var activeNetworkInfo = cm.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        Log.i("update_statut", "Network is available : true")
                        return true
                    } else {
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "isConnectedToInternet: ${e.message}")
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ")
        return false
    }
}

