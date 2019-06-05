package com.cleveroad.aropensource.utils.simple

import android.text.Editable
import android.text.TextWatcher

open class SimpleTextWatcher(private val actionAfterTextChanged: (text: String) -> Unit = {},
                             private val actionBeforeTextChanged: (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit =
                                     { _: CharSequence?, _: Int, _: Int, _: Int -> },
                             private val actionOnTextChanged: (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit =
                                     { _: CharSequence?, _: Int, _: Int, _: Int -> }) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        actionAfterTextChanged(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        actionBeforeTextChanged(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        actionOnTextChanged(s, start, before, count)
    }
}