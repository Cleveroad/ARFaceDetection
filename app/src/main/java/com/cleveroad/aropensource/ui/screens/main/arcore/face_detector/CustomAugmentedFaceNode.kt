package com.cleveroad.aropensource.ui.screens.main.arcore.face_detector

import android.content.Context
import android.widget.Toast
import com.cleveroad.aropensource.R
import com.cleveroad.bootstrap.kotlin_ext.applyIf
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode


class CustomAugmentedFaceNode(augmentedFace: AugmentedFace?, private val context: Context) :
    AugmentedFaceNode(augmentedFace) {

    companion object {
        private const val HALF_DIVIDER = 2
    }

    private var logoNode: Node? = null

    override fun onActivate() {
        super.onActivate()
        logoNode = Node()
        logoNode?.setParent(this)
        logoNode?.isEnabled = isTracking()
        ViewRenderable.builder()
            .setView(context, R.layout.logo_view)
            .build()
            .thenAccept { renderable ->
                logoNode?.renderable = renderable
            }
            .exceptionally { throwable ->
                Toast.makeText(context, "Could not load plane card view.", Toast.LENGTH_LONG).show()
                throw AssertionError("Could not load plane card view.", throwable)
            }
    }

    private fun isTracking() =
        augmentedFace != null && augmentedFace?.trackingState == TrackingState.TRACKING

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        logoNode?.isEnabled = isTracking().applyIf({ true }) {
            augmentedFace?.run {
                logoNode?.worldRotation = worldRotation
                val rightHeard = getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
                val leftHeard = getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT)

                val zCoordinate =
                    (leftHeard.tz() + rightHeard.tz()) / HALF_DIVIDER + (getRegionPose(AugmentedFace.RegionType.NOSE_TIP).tz() - centerPose.tz()) * -1
                logoNode?.worldPosition = Vector3(
                    (leftHeard.tx() + rightHeard.tx()) / HALF_DIVIDER,
                    (leftHeard.ty() + rightHeard.ty()) / HALF_DIVIDER,
                    zCoordinate
                )
            }
        }
    }
}