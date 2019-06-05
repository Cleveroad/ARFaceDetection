package com.packagename.extensions

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup

private const val DEFAULT_CLICK_DEBOUNCE_TIME = 500L

fun View.applyLayoutParams(block: ViewGroup.MarginLayoutParams.() -> Unit) =
        (layoutParams as? ViewGroup.MarginLayoutParams)
                ?.let { layoutParams = it.apply { block(this) } }

fun View.clickWithDebounce(debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime = 0L
        override fun onClick(v: View) {
            SystemClock.elapsedRealtime().takeIf { it - lastClickTime > debounceTime }
                    ?.run {
                        action()
                        lastClickTime = this
                    }
        }
    })
}

fun View.OnClickListener.setClickListenerWithDebounce(vararg views: View, debounceTime: Long = DEFAULT_CLICK_DEBOUNCE_TIME) {
    val clickListener = object : View.OnClickListener {
        private var lastClickTime = 0L
        override fun onClick(v: View) {
            SystemClock.elapsedRealtime()
                    .takeIf { it - lastClickTime > debounceTime }
                    ?.run {
                        this@setClickListenerWithDebounce.onClick(v)
                        lastClickTime = this
                    }
        }
    }
    views.forEach { view -> view.setOnClickListener(clickListener) }
}