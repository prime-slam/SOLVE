package sliv.tool.parsers.landmark_factories

import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer
import sliv.tool.parsers.structures.Point as StoragePoint
import sliv.tool.scene.model.Landmark.Keypoint as ScenePoint

object PointFactory : LandmarkFactory<StoragePoint, ScenePoint> {
    override fun buildLandmark(storageFormatLandmark: StoragePoint, containingLayer: Layer): ScenePoint {
        val point = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x, storageFormatLandmark.y)

        return ScenePoint(storageFormatLandmark.uid, containingLayer, point)
    }
}
