package com.packagename.ui.listeners

import com.google.android.material.textfield.TextInputLayout
import com.packagename.utils.EMPTY_STRING
import com.packagename.utils.simple.SimpleTextWatcher
import java.lang.ref.WeakReference

class HideErrorTextWatcher(til: TextInputLayout) : SimpleTextWatcher() {

    private val ref = WeakReference<TextInputLayout>(til)

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        ref.get()?.run {
            error = EMPTY_STRING
            isErrorEnabled = false
        }
    }
}