package com.cleveroad.aropensource.utils

import android.util.Log
import com.cleveroad.aropensource.BuildConfig

object LOG {

    private const val DEFAULT_TAG = ""
    private const val DEFAULT_MESSAGE = ""

    fun e(tag: String = DEFAULT_TAG, message: String? = DEFAULT_MESSAGE, throwable: Throwable? = null) {
        if (isDebug) {
            throwable?.run {
                Log.e(tag, message ?: this.message, this)
            } ?: message?.let { Log.e(tag, it) }
        }
    }

    fun d(tag: String = DEFAULT_TAG, message: String? = DEFAULT_MESSAGE, throwable: Throwable? = null) {
        if (isDebug) {
            throwable?.run {
                Log.d(tag, message ?: this.message, this)
            } ?: message?.let { Log.d(tag, it) }
        }
    }

    fun w(tag: String = DEFAULT_TAG, message: String? = DEFAULT_MESSAGE, throwable: Throwable? = null) {
        if (isDebug) {
            throwable?.run {
                Log.w(tag, message ?: this.message, this)
            } ?: message?.let { Log.w(tag, it) }
        }
    }

    fun i(tag: String = DEFAULT_TAG, message: String? = DEFAULT_MESSAGE, throwable: Throwable? = null) {
        if (isDebug) {
            throwable?.run {
                Log.i(tag, message ?: this.message, this)
            } ?: message?.let { Log.i(tag, it) }
        }
    }

    fun v(tag: String = DEFAULT_TAG, message: String? = DEFAULT_MESSAGE, throwable: Throwable? = null) {
        if (isDebug) {
            throwable?.run {
                Log.v(tag, message ?: this.message, this)
            } ?: message?.let { Log.v(tag, it) }
        }
    }

    private val isDebug: Boolean
        get() = BuildConfig.DEBUG
}
