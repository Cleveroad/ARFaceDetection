package com.cleveroad.aropensource

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class ARApp : Application() {

    companion object {
        lateinit var instance: ARApp
            private set
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}