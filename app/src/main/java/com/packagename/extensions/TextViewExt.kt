package com.packagename.extensions

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.show
import com.packagename.R
import com.packagename.utils.EMPTY_STRING

private val updateDefaultDrawState: (TextPaint) -> Unit = {
    with(it) {
        color = getColorApp(R.color.teal_500)
        isUnderlineText = false
        bgColor = getColorApp(R.color.bg_white)
    }
}


/**
 * The method attaches objects [ClickableSpan] to parts of the text to handle the clicks.
 *
 * @param initText some text [String] which need add to start of line
 * @param separateText some text [String] which will be used for separating clickable text
 * @param updateDrawState use for set extra parameter to clickable text
 * @param onClick handle click
 *
 */
fun <T> TextView.addClickableText(initText: String,
                                  separateText: String,
                                  clickableText: List<Pair<T, String>>,
                                  updateDrawState: (TextPaint) -> Unit = updateDefaultDrawState,
                                  onClick: (T) -> Unit) {
    text = clickableText.foldIndexed(TextUtils.concat(initText)) { pos, acc, item ->
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                onClick(item.first)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                updateDrawState(ds)
            }
        }
        SpannableString(item.second).run {
            setSpan(clickableSpan, 0, this.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (pos == 0) TextUtils.concat(acc, this) else TextUtils.concat(acc, separateText, this)
        }
    }
    movementMethod = LinkMovementMethod.getInstance()
}

/**
 * Hide [TextView] if @param[string] is empty
 *
 * @param string [String] text which must shown in TextView
 * @param isGone [Boolean] if true TextView will be gone, else invisible
 */
fun TextView.hideIfEmpty(string: String?, isGone: Boolean = true) =
        string.takeUnless { it.isNullOrBlank() }
                ?.let {
                    show()
                    text = it
                }
                ?: run {
                    text = EMPTY_STRING
                    hide(isGone)
                }
