package com.example.smartparent.data.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import java.io.IOException
import java.net.InetAddress

class NetworkConnectivityViewModel(context: Context) : LiveData<Boolean>() {
    override fun onActive() {
        super.onActive()
        checkNetworkConnectivity()
        checkInternetConnectivity()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

    }

    private fun checkNetworkConnectivity() {
        val network = connectivityManager.activeNetwork
        if (network == null) postValue(false)

        val requestBuilder = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        }.build()
        connectivityManager.registerNetworkCallback(requestBuilder, networkCallback)

    }

    private fun checkInternetConnectivity() {
        Thread {
            try {
                val address = InetAddress.getByName("8.8.8.8")
                val reachable = address.isReachable(3000)
                postValue(reachable)

            } catch (e: IOException) {
                postValue(false)
            }
        }.start()

    }

}