package solve.rendering.engine.core.input

import org.joml.Vector2i
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.toVector2i
import solve.scene.model.Landmark.Keypoint

class PointLayerClickHandler : LayerClickHandler<Keypoint>() {
    override fun indexOfClickedLandmark(landmarks: List<Keypoint>, clickedPixelCoordinate: Vector2i): Int {
        var minKeypointDistanceIndex = -1
        var minKeypointDistance = Double.MAX_VALUE

        landmarks.forEachIndexed { index, keypoint ->
            val keypointCoordinate = keypoint.coordinate.toVector2i()
            val keypointDistance = (keypointCoordinate - clickedPixelCoordinate).length()
            if (keypointDistance < ClickedLandmarkMaxAllowedDistance && keypointDistance < minKeypointDistance) {
                minKeypointDistanceIndex = index
                minKeypointDistance = keypointDistance
            }
        }

        return minKeypointDistanceIndex
    }

    companion object {
        private const val ClickedLandmarkMaxAllowedDistance = 3
    }
}