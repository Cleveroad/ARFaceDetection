package com.cleveroad.aropensource.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cleveroad.arfacedetector.ui.screens.main.arcore.face_detector.AugmentedFacesFragment
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.FaceDetectorFragment
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleActivity
import com.cleveroad.aropensource.ui.screens.main.chooser.InstrumentsCallback
import com.cleveroad.aropensource.ui.screens.main.chooser.InstrumentsFragment

class MainActivity : BaseLifecycleActivity(), InstrumentsCallback {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showInstrumentsScreen()
    }

    private fun showInstrumentsScreen() {
        replaceFragment(InstrumentsFragment.newInstance(), false)
    }

    override fun mlKitlSelected() {
        replaceFragment(FaceDetectorFragment.newInstance(R.drawable.ic_joincleveroad_medium))
    }

    override fun arCoreSelected() {
        replaceFragment(AugmentedFacesFragment.newInstance(R.drawable.ic_joincleveroad_medium))
    }
}