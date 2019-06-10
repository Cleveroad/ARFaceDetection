package com.cleveroad.aropensource.ui.screens.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.aropensource.ui.base.BaseVM
import com.cleveroad.bootstrap.kotlin_core.utils.ioToMain
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class SplashVM(application: Application) : BaseVM(application) {
    companion object {
        private const val SPLASH_DELAY = 2_000L
    }

    val timerLD = MutableLiveData<Unit>()

    fun startTimer() {
        Single.timer(SPLASH_DELAY, TimeUnit.MILLISECONDS)
            .compose { ioToMain(it) }
            .subscribe { _, _ ->
                timerLD.value = Unit
            }.addSubscription()
    }
}