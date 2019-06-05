package com.packagename.network.exceptions

import com.packagename.R
import com.packagename.extensions.getStringApp

class NoNetworkException : Exception() {

    companion object {
        // TODO change no internet connection error message
        private val ERROR_MESSAGE = getStringApp(R.string.no_internet_connection_error)
    }

    override val message: String = ERROR_MESSAGE
}
