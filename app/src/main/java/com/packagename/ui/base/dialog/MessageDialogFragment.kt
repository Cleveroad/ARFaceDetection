package com.packagename.ui.base.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.packagename.R
import com.packagename.extensions.clickWithDebounce
import com.packagename.extensions.hideIfEmpty
import com.packagename.ui.base.FragmentArgumentDelegate
import kotlinx.android.synthetic.main.dialog_fragment_message.*

class MessageDialogFragment : SimpleDialogFragment() {
    override val layoutId = R.layout.dialog_fragment_message
    override val gravity = Gravity.CENTER
    override val dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT
    override val dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT

    companion object {
        fun newInstance(title: String? = null, message: String? = null) =
                MessageDialogFragment().apply {
                    this.title = title
                    this.message = message
                }
    }

    private var title by FragmentArgumentDelegate<String>()
    private var message by FragmentArgumentDelegate<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bOk.clickWithDebounce { dismiss() }
        tvTitle.hideIfEmpty(title)
        tvTitle.hideIfEmpty(message)
    }

}