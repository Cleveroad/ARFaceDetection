package com.cleveroad.aropensource.utils

import android.os.Build
import android.util.Log
import com.cleveroad.aropensource.BuildConfig
import java.util.regex.Pattern


object Logger {
    private const val callStackIndex = 2

    private const val maxTagLength = 23

    private const val pattern = "(\\$\\d+)+$"

    private var url = EMPTY_STRING

    fun v(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.v(tag, message() + url, exception)
    }

    fun d(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.d(tag, message() + url, exception)
    }

    fun i(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.i(tag, message() + url, exception)
    }

    fun w(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.w(tag, message() + url, exception)
    }

    fun e(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.e(tag, message() + url, exception)
    }

    fun wtf(exception: Exception? = null, callStackLevel: Int = 0, tag: String = callerTag(callStackLevel), message: () -> String) = inDebug {
        Log.wtf(tag, message() + url, exception)
    }

    private inline fun inDebug(action: () -> Unit) {
        if (BuildConfig.DEBUG) {
            action()
        }
    }

    /**
     * @return The class name for the calling class as a String.
     */
    private fun callerTag(callStackLevel: Int = 0): String {
        var tag: String
        var callStackLevelLocale = callStackLevel
        do {
            val stackTrace = Throwable().stackTrace
            tag = stackTrace[callStackIndex + callStackLevelLocale].className
            val matcher = Pattern.compile(pattern).matcher(tag)

            if (matcher.find()) tag = matcher.replaceAll("")

            tag = tag.substring(tag.lastIndexOf('.') + 1)

            url = "(${stackTrace[callStackIndex + callStackLevelLocale].fileName}:${stackTrace[callStackIndex + callStackLevelLocale].lineNumber})"
            callStackLevelLocale++
        } while (url.contains("AnyExtensions"))
        // Tag length limit was removed in API 24.
        return if (tag.length <= maxTagLength || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else {
            tag.substring(0, maxTagLength)
        }
    }
}