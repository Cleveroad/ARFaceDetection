package com.cleveroad.aropensource.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

internal const val NO_FLAGS = 0

fun Activity.hideKeyboard() =
    currentFocus?.let {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.run inputMethodManager@{
            this@inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, NO_FLAGS)
        }
    } ?: false

fun Fragment.hideKeyboard() = activity?.hideKeyboard() ?: false