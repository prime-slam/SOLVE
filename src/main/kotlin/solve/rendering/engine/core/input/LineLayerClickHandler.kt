package solve.rendering.engine.core.input

import org.joml.Vector2i
import solve.rendering.engine.utils.pointToSegmentDistance
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toVector2i
import solve.scene.model.Landmark.Line

class LineLayerClickHandler : LayerClickHandler<Line>() {
    override fun indexOfClickedLandmark(landmarks: List<Line>, clickedPixelCoordinate: Vector2i): Int {
        var minLineDistanceIndex = -1
        var minLineDistance = Float.MAX_VALUE

        landmarks.forEachIndexed { index, line ->
            val lineStartCoordinate = line.startCoordinate.toVector2i()
            val lineFinishCoordinate = line.finishCoordinate.toVector2i()
            val lineDistance = pointToSegmentDistance(
                clickedPixelCoordinate.toFloatVector(),
                lineStartCoordinate.toFloatVector(),
                lineFinishCoordinate.toFloatVector()
            )
            if (lineDistance < ClickedLandmarkMaxAllowedDistance && lineDistance < minLineDistance) {
                minLineDistanceIndex = index
                minLineDistance = lineDistance
            }
        }

        return minLineDistanceIndex
    }

    companion object {
        private const val ClickedLandmarkMaxAllowedDistance = 3
    }
}