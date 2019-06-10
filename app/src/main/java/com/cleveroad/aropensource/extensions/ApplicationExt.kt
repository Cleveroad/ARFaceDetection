package com.cleveroad.aropensource.extensions

import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.cleveroad.aropensource.ARApp

fun getStringApp(resId: Int) = ARApp.instance.getString(resId)

fun getAppString(@StringRes stringId: Int, vararg formatArgs: Any) =
        ARApp.instance.getString(stringId, *formatArgs)

fun getStringArray(@ArrayRes id: Int) = ARApp.instance.resources.getStringArray(id)

fun getDrawableApp(@DrawableRes resId: Int) = ContextCompat.getDrawable(ARApp.instance, resId)

fun getColorApp(@ColorRes colorRes: Int) = ContextCompat.getColor(ARApp.instance, colorRes)