package com.cleveroad.aropensource.ui.base

import android.content.Intent
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleActivity
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_core.ui.BlockedCallback
import com.google.android.material.snackbar.Snackbar
import com.cleveroad.R
import com.cleveroad.aropensource.ui.base.dialog.DialogFragmentCallback

abstract class BaseActivity<T : BaseLifecycleViewModel> : BaseLifecycleActivity<T>(),
        BlockedCallback, DialogFragmentCallback {

    override fun getProgressBarId() = R.id.progressView

    override fun getSnackBarDuration() = Snackbar.LENGTH_SHORT

    override fun onBlocked() = Unit

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        val fragment = supportFragmentManager.findFragmentById(containerId)
        if (fragment is DialogFragmentCallback) fragment.onDialogResult(requestCode, resultCode, data)
    }
}