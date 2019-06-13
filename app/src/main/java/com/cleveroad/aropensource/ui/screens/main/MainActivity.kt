package com.cleveroad.aropensource.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleActivity
import com.cleveroad.aropensource.ui.screens.main.arcore.CameraPreviewFragment
import com.cleveroad.aropensource.ui.screens.main.arcore.face_detector.AugmentedFacesFragment
import com.cleveroad.aropensource.ui.screens.main.chooser.InstrumentsCallback
import com.cleveroad.aropensource.ui.screens.main.chooser.InstrumentsFragment
import com.cleveroad.aropensource.ui.screens.main.mlkit.FaceDetectorFragment

class MainActivity : BaseLifecycleActivity<MainVM>(), InstrumentsCallback {

    companion object {
        fun start(context: Context) = context.run {
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }.let {
                startActivity(it)
            }
        }
    }

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_main

    override val viewModelClass = MainVM::class.java

    override fun observeLiveData() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showInstrumentsScreen()
    }

    private fun showInstrumentsScreen() {
        replaceFragment(InstrumentsFragment.newInstance(), false)
    }

    private fun showCameraPreviewScreen() {
        replaceFragment(CameraPreviewFragment.newInstance())
    }

    override fun mlKitlSelected() {
        replaceFragment(FaceDetectorFragment.newInstance())
    }

    override fun arCoreSelected() {
        replaceFragment(AugmentedFacesFragment.newInstance())
    }
}