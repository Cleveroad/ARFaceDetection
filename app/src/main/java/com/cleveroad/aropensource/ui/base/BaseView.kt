package com.cleveroad.aropensource.ui.base

import androidx.annotation.StringRes
import com.cleveroad.aropensource.R


interface BaseView {

    fun showProgress()

    fun showProgress(isShow: Boolean)

    fun hideProgress()

    fun onError(error: Any)

    fun showSnackbar(message: String)

    fun showSnackbar(@StringRes res: Int)

    fun showAlert(
        message: String,
        title: String? = null,
        cancelable: Boolean = true,
        positiveRes: Int = R.string.ok,
        positiveFun: () -> Unit = {},
        negativeRes: Int? = R.string.no,
        negativeFun: () -> Unit = {}
    )

    fun showAlert(
        message: Int,
        title: Int? = null,
        cancelable: Boolean = true,
        positiveRes: Int = R.string.ok,
        positiveFun: () -> Unit = {},
        negativeRes: Int? = null,
        negativeFun: () -> Unit = {}
    )

    fun hideSnackBar()

    fun showSnackBar(@StringRes res: Int, @StringRes actionRes: Int, callback: () -> Unit)

    fun showSnackBar(text: CharSequence, @StringRes actionRes: Int, callback: () -> Unit)
}