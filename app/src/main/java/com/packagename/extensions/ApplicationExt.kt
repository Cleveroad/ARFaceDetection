package com.packagename.extensions

import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.packagename.NPApp

fun getStringApp(resId: Int) = NPApp.instance.getString(resId)

fun getAppString(@StringRes stringId: Int, vararg formatArgs: Any) =
        NPApp.instance.getString(stringId, *formatArgs)

fun getStringArray(@ArrayRes id: Int) = NPApp.instance.resources.getStringArray(id)

fun getDrawableApp(@DrawableRes resId: Int) = ContextCompat.getDrawable(NPApp.instance, resId)

fun getColorApp(@ColorRes colorRes: Int) = ContextCompat.getColor(NPApp.instance, colorRes)