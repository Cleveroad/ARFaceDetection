package com.cleveroad.aropensource.ui.screens.main.face_detector

import android.content.Context
import android.widget.Toast
import com.cleveroad.aropensource.R
import com.cleveroad.bootstrap.kotlin_ext.applyIf
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode

class CustomAugmentedFaceNode(augmentedFace: AugmentedFace?, private val context: Context) :
    AugmentedFaceNode(augmentedFace) {

    companion object {
        private const val Y_POSITION_OFFSET = 0.07f
    }

    private var logoNode: Node? = null

    override fun onActivate() {
        super.onActivate()
        logoNode = Node()
        logoNode?.setParent(this)
        logoNode?.isEnabled = isTracking()

        augmentedFace?.run augmentedFace@{
            val foreHeadRightPos = getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
            logoNode?.localPosition =
                Vector3(centerPose.tx(), foreHeadRightPos.ty() + Y_POSITION_OFFSET, centerPose.tz())
        }

        ViewRenderable.builder()
            .setView(context, R.layout.image_view)
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
                val foreHeadRightPos = getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT)
                centerPose?.run centerPose@{
                    logoNode?.worldPosition = Vector3(tx(), foreHeadRightPos.ty() + Y_POSITION_OFFSET, tz())
                    logoNode?.worldRotation = Quaternion(qx(), qy(), qz(), qw())
                }
            }
        }
    }
}