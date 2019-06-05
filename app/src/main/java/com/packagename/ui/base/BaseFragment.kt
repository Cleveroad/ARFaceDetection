package com.packagename.ui.base

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleFragment
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_ext.withNotNull
import com.packagename.BuildConfig
import com.packagename.R
import com.packagename.ui.base.dialog.DialogFragmentCallback

abstract class BaseFragment<T : BaseLifecycleViewModel> : BaseLifecycleFragment<T>(),
        DialogFragmentCallback {

    companion object {
        private const val KEYBOARD_VISIBLE_THRESHOLD_DP = 300
    }

    private val keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
        withNotNull(view) {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)
            val heightChangView = rootView.height - (rect.bottom - rect.top)
            when {
                !blockKeyboardListener && heightChangView > KEYBOARD_VISIBLE_THRESHOLD_DP -> {
                    blockKeyboardListener = true
                    onKeyboardSwitch(true)
                }
                blockKeyboardListener && heightChangView <= KEYBOARD_VISIBLE_THRESHOLD_DP -> {
                    blockKeyboardListener = false
                    onKeyboardSwitch(false)
                }
            }
        }
    }
    private var blockKeyboardListener: Boolean = true

    override var endpoint = BuildConfig.ENDPOINT

    override var versionName = BuildConfig.VERSION_NAME

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (needKeyboardListener()) view.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
    }

    override fun onDestroyView() {
        if (needKeyboardListener()) view?.viewTreeObserver?.removeOnGlobalLayoutListener(keyboardListener)
        super.onDestroyView()
    }

    override fun getVersionsLayoutId() = R.id.versionsContainer

    override fun getEndPointTextViewId() = R.id.tvEndpoint

    override fun getVersionsTextViewId() = R.id.tvVersion

    override fun isDebug() = BuildConfig.DEBUG

    /**
     * Display a warning when going to action back
     */
    override fun showBlockBackAlert() = Unit

    /**
     * Display any errors that happens in viewModel
     */
    override fun onError(error: Any) = Unit

    /**
     * Turn on/off keyboard listener
     */
    protected open fun needKeyboardListener() = false

    /**
     * This method is called when keyboard change state
     */
    protected open fun onKeyboardSwitch(isShow: Boolean) = Unit

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) = Unit

}