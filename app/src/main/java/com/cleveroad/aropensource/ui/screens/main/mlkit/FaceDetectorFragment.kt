package com.cleveroad.aropensource.ui.screens.main.mlkit

import android.os.Bundle
import android.view.View
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment


class FaceDetectorFragment : BaseLifecycleFragment<FaceDetectorVM>() {

    companion object {
        fun newInstance() =
            FaceDetectorFragment().apply {
                arguments = Bundle()
            }
    }

    override val viewModelClass = FaceDetectorVM::class.java

    override val layoutId = R.layout.ml_kit_face_detector_fragment

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}