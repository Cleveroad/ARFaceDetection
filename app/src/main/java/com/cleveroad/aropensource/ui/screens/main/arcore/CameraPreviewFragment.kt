package com.cleveroad.aropensource.ui.screens.main.arcore

import android.os.Bundle
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseFragment
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TITLE
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TOOLBAR

class CameraPreviewFragment : BaseFragment<CameraPreviewVM>() {

    companion object {
        fun newInstance() =
            CameraPreviewFragment().apply {
                arguments = Bundle()
            }
    }

    override val layoutId = R.layout.fragment_camera_preview

    override val viewModelClass = CameraPreviewVM::class.java

    override fun getScreenTitle() = NO_TITLE

    override fun getToolbarId() = NO_TOOLBAR

    override fun hasToolbar() = false

    override fun observeLiveData(viewModel: CameraPreviewVM) = Unit

}