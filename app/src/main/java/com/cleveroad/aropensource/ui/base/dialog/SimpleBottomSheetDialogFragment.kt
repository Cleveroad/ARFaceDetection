package com.cleveroad.aropensource.ui.base.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Base class for implement [BottomSheetDialog]
 */
abstract class BottomSheetDialogFragment : SimpleDialogFragment() {

    override val dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT
    override val dialogWidth = ViewGroup.LayoutParams.WRAP_CONTENT

    override val gravity = Gravity.BOTTOM

    override fun onCreateDialog(savedInstanceState: Bundle?) =
            BottomSheetDialog(requireContext(), theme)

}