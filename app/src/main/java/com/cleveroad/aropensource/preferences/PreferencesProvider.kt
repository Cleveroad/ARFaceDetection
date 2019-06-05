package com.cleveroad.aropensource.preferences

internal object PreferencesProvider {

    var token by StringPD()

    var refreshToken by StringPD()

    fun saveSession(token: String?, refreshToken: String?) {
        PreferencesProvider.token = token
        PreferencesProvider.refreshToken = refreshToken
    }

    fun clearData() {
        token = null
        refreshToken = null
    }

}