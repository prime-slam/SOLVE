package sliv.tool.parsers.factories

import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer
import sliv.tool.parsers.structures.Point as StoragePoint
import sliv.tool.scene.model.Landmark.Keypoint as ScenePoint

object PointFactory : LandmarkFactory<StoragePoint, ScenePoint, Layer.PointLayer> {
    override fun buildLandmark(storageFormatLandmark: StoragePoint, containingLayer: Layer.PointLayer): ScenePoint {
        val point = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x, storageFormatLandmark.y)

        return ScenePoint(storageFormatLandmark.uid, containingLayer, point)
    }
}
