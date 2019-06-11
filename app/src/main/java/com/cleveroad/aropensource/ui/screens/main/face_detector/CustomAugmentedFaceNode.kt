package com.cleveroad.aropensource.ui.screens.main.face_detector

import android.content.Context
import android.widget.Toast
import com.cleveroad.aropensource.R
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode

class CustomAugmentedFaceNode(augmentedFace: AugmentedFace?, private val context: Context) :
    AugmentedFaceNode(augmentedFace) {

    private var logoNode: Node? = null

    override fun onActivate() {
        super.onActivate()

        if (logoNode == null) {
            logoNode = Node()
            logoNode?.setParent(this)
            logoNode?.isEnabled = true
            augmentedFace?.centerPose?.let {
                logoNode?.localPosition = Vector3(it.tx(), 0.3f, it.tz())
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
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)
        if (scene == null) return
        augmentedFace?.centerPose?.let {

//            logoNode?.worldRotation = Quaternion(it.qx(), it.qy(), it.qz(), it.qw())
//            logoNode?.worldPosition = Vector3(it.tx(), 0.1f, it.tz())

            val cameraPosition = scene?.camera?.worldPosition
            val cardPosition = Vector3(it.tx(), 0.3f, it.tz())

            val direction = Vector3.subtract(cameraPosition, cardPosition)
            val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
            logoNode?.setWorldRotation(lookRotation)
        }


//        augmentedFace?.centerPose?.let {
//            logoNode?.worldPosition = Vector3(it.tx(), 0.1f, it.tz())
//            logoNode?.worldRotation = Quaternion(it.qx(), it.qy(), it.qz(), it.qw())
//        }
//        if (scene == null) return
//        val cameraPosition = scene?.camera?.worldPosition
//        val cardPosition = logoNode?.getWorldPosition()
//        val direction = Vector3.subtract(cameraPosition, cardPosition)
//        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
//        logoNode?.setWorldRotation(lookRotation)
    }
}