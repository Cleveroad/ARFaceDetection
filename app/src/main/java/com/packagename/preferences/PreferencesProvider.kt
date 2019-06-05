package com.packagename.preferences

internal object PreferencesProvider {

    var token by StringPD()

    var refreshToken by StringPD()

    fun saveSession(token: String?, refreshToken: String?) {
        this.token = token
        this.refreshToken = refreshToken
    }

    fun clearData() {
        token = null
        refreshToken = null
    }

}