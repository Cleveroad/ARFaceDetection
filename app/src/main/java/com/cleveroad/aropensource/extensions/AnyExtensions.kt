package com.cleveroad.aropensource.extensions

import com.cleveroad.aropensource.utils.EMPTY_STRING
import com.cleveroad.aropensource.utils.Logger

/**
 * Print log error
 *
 * @param text [String] It`s text to print in logs
 * @param callLevel [Int] This is the level from which starts print exception tree
 *
 * @return [T] return the object from with it method was called
 */
fun <T> T?.printLogE(text: String? = EMPTY_STRING, callLevel: Int = 1) = apply {
    Logger.run {
        e(message = { "$text ${this@printLogE}" }, callStackLevel = callLevel)
    }
}

/**
 * Print log debug
 *
 * @param text [String] It`s text to print in logs
 * @param callLevel [Int] This is the level from which starts print exception tree
 *
 * @return [T] return the object from with it method was called
 */
fun <T> T?.printLog(text: String? = EMPTY_STRING, callLevel: Int = 1) = apply {
    Logger.run {
        d(message = { "$text ${this@printLog}" }, callStackLevel = callLevel)
    }
}