package com.cleveroad.aropensource.ui.screens.main.arcore.face_detector

import android.os.Bundle
import android.view.View
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.cleveroad.aropensource.utils.ArUtils
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.Renderable


class AugmentedFacesFragment : BaseLifecycleFragment<AugmentedFacesVM>() {

    companion object {
        fun newInstance() =
            AugmentedFacesFragment().apply {
                arguments = Bundle()
            }
    }

    override val viewModelClass = AugmentedFacesVM::class.java

    override val layoutId = R.layout.fragment_augmented_faces

    private var arFragment: FaceDetectorFragment? = null

    private val faceNodeMap = HashMap<AugmentedFace, CustomAugmentedFaceNode>()

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!ArUtils.checkIsSupportedDeviceOrFinish(requireContext())) return
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
                faceNodeMap[face] =
                    CustomAugmentedFaceNode(face, requireContext())
                        .apply {
                            setParent(sceneView.scene)
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