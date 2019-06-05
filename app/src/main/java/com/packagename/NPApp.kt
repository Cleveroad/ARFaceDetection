package com.packagename

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.facebook.stetho.Stetho
import com.packagename.models.Session
import com.packagename.models.SessionModel
import com.packagename.preferences.PreferencesProvider
import com.securepreferences.SecurePreferences


// TODO rename application class
class NPApp : Application() {

    companion object {
        lateinit var instance: NPApp
            private set
        lateinit var prefs: SharedPreferences
    }

    private var currentSession: Session = SessionModel()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            getSharedPreferences()
        } else {
            getSecurePreferences()
        }
    }

    fun setSession(session: Session?) {
        session?.let { currentSession = it }
    }

    fun saveSession() {
        currentSession.run {
            safeLet(accessToken, refreshToken) { token, rfToken ->
                PreferencesProvider.saveSession(token, rfToken)
            }
        }
    }

    fun getSession() = currentSession

    fun onLogout() {
        // TODO need implemented logout
        PreferencesProvider.clearData()
    }

    private fun getSharedPreferences() = instance.applicationContext
            .getSharedPreferences(BuildConfig.SECURE_PREF_NAME, Context.MODE_PRIVATE)

    private fun getSecurePreferences() = SecurePreferences(this,
            BuildConfig.SECURE_PREF_PASSWORD,
            BuildConfig.SECURE_PREF_NAME)
    
}
