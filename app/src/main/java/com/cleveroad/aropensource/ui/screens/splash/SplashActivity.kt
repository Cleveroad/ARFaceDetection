package com.cleveroad.aropensource.ui.screens.splash

import androidx.lifecycle.Observer
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleActivity
import com.cleveroad.aropensource.ui.screens.main.MainActivity

class SplashActivity : BaseLifecycleActivity<SplashVM>() {

    override val viewModelClass = SplashVM::class.java

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_splash

    private val timerObserver = Observer<Unit> {
        openOtherScreen()
    }

    override fun observeLiveData() {
        with(viewModel) {
            timerLD.observe(this@SplashActivity, timerObserver)
            startTimer()
        }
    }

    private fun openOtherScreen() {
        MainActivity.start(this)
        finish()
    }
}