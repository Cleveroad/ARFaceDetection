package com.cleveroad.arfacedetector.ui.screens.main.arcore.face_detector

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import com.cleveroad.arfacedetector.R
import com.cleveroad.arfacedetector.ui.base.BaseLifecycleFragment
import com.cleveroad.arfacedetector.ui.base.safeLet
import com.cleveroad.arfacedetector.utils.ArUtils
import com.cleveroad.arfacedetector.utils.BitmapUtils
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.Renderable


class AugmentedFacesFragment : BaseLifecycleFragment() {

    companion object {
        private const val RES_ID_EXTRA = "res_id"

        fun newInstance(@DrawableRes resId: Int) =
                AugmentedFacesFragment().apply {
                    arguments = Bundle().apply {
                        putInt(RES_ID_EXTRA, resId)
                    }
                }
    }

    override val layoutId = R.layout.fragment_augmented_faces

    private var arFragment: FaceDetectorFragment? = null

    private val faceNodeMap = HashMap<AugmentedFace, CustomAugmentedFaceNode>()

    private var bitmap: Bitmap? = null

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!ArUtils.checkIsSupportedDevice(requireContext())) return

        bitmap = safeLet(arguments?.getInt(RES_ID_EXTRA), context) { resId, context ->
            BitmapUtils.getBitmapFromVectorDrawable(context, resId)
        }

        arFragment = childFragmentManager.findFragmentById(R.id.face_fragment) as? FaceDetectorFragment

        arFragment?.arSceneView?.apply sceneView@{
            // This is important to make sure that the camera stream renders first so that
            // the face mesh occlusion works correctly.
            cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

            scene?.addOnUpdateListener { _ ->
                getFaces(this@sceneView)
                removeFaces()
            }
        }
    }

    /**
     * Make new AugmentedFaceNodes for any new faces.
     */
    private fun getFaces(sceneView: ArSceneView) {
        sceneView.session?.getAllTrackables(AugmentedFace::class.java)?.forEach { face ->
            if (!faceNodeMap.containsKey(face)) {
                safeLet(bitmap, context) { bitmap, context ->
                    faceNodeMap[face] =
                            CustomAugmentedFaceNode(face, context, bitmap)
                                    .apply {
                                        setParent(sceneView.scene)
                                    }
                }
            }
        }
    }

    /**
     * Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
     */
    private fun removeFaces() {
        faceNodeMap.entries.iterator().run {
            while (hasNext()) {
                val entry = next()
                if (entry.key.trackingState == TrackingState.STOPPED) {
                    entry.value.setParent(null)
                    remove()
                }
            }
        }
    }
}