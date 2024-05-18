package solve.rendering.engine.core.input

import javafx.scene.robot.Robot
import org.joml.Vector2i
import solve.rendering.engine.utils.minus
import solve.rendering.engine.utils.pointToSegmentDistance
import solve.rendering.engine.utils.toFloatVector
import solve.rendering.engine.utils.toVector2i
import solve.scene.model.Landmark
import java.awt.Color
import java.awt.MouseInfo

object MouseInputHandler {
    private const val LineHandledDistanceMultiplier = 4f
    private const val PointHandledDistanceMultiplier = 1.5f

    private const val ColorSegmentMaxValue = 255

    fun indexOfClickedLineLandmark(
        lineLandmarks: List<Landmark.Line>,
        clickedPixelCoordinate: Vector2i,
        lineWidth: Float
    ): Int {
        var minLineDistanceIndex = -1
        var minLineDistance = Float.MAX_VALUE

        lineLandmarks.forEachIndexed { index, line ->
            val lineStartCoordinate = line.startCoordinate.toVector2i()
            val lineFinishCoordinate = line.finishCoordinate.toVector2i()
            val lineDistance = pointToSegmentDistance(
                clickedPixelCoordinate.toFloatVector(),
                lineStartCoordinate.toFloatVector(),
                lineFinishCoordinate.toFloatVector()
            )
            if (lineDistance < lineWidth * LineHandledDistanceMultiplier && lineDistance < minLineDistance) {
                minLineDistanceIndex = index
                minLineDistance = lineDistance
            }
        }

        return minLineDistanceIndex
    }

    fun indexOfClickedPointLandmark(
        pointLandmarks: List<Landmark.Keypoint>,
        clickedPixelCoordinate: Vector2i,
        pointLandmarkSize: Float
    ): Int {
        var minKeypointDistanceIndex = -1
        var minKeypointDistance = Double.MAX_VALUE

        pointLandmarks.forEachIndexed { index, keypoint ->
            val keypointCoordinate = keypoint.coordinate.toVector2i()
            val keypointDistance = (keypointCoordinate - clickedPixelCoordinate).length()
            if (keypointDistance < pointLandmarkSize * PointHandledDistanceMultiplier &&
                keypointDistance < minKeypointDistance
            ) {
                minKeypointDistanceIndex = index
                minKeypointDistance = keypointDistance
            }
        }

        return minKeypointDistanceIndex
    }

    fun getClickedPixelColor() : Color {
        val pointerLocation = MouseInfo.getPointerInfo().location
        val robot = Robot()

        val javaFXColor = robot.getPixelColor(pointerLocation.x.toDouble(), pointerLocation.y.toDouble())

        return Color(
            (javaFXColor.red * ColorSegmentMaxValue).toInt(),
            (javaFXColor.green * ColorSegmentMaxValue).toInt(),
            (javaFXColor.blue * ColorSegmentMaxValue).toInt()
        )
    }
}
