package com.cleveroad.aropensource.ui.screens.splash

import android.os.Bundle
import android.os.Handler
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleActivity
import com.cleveroad.aropensource.ui.screens.main.MainActivity

class SplashActivity : BaseLifecycleActivity() {

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_splash

    private val handler = Handler {
        openOtherScreen()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.sendEmptyMessageDelayed(0, 2000)
    }

    private fun openOtherScreen() {
        MainActivity.start(this)
        finish()
    }
}