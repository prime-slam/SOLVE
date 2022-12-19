package solve.parsers.factories

import solve.parsers.ParserUtils
import solve.scene.model.LayerSettings.PointLayerSettings
import solve.parsers.structures.Point as StoragePoint
import solve.scene.model.Landmark.Keypoint as ScenePoint

object PointFactory : LandmarkFactory<StoragePoint, ScenePoint, PointLayerSettings> {
    override fun buildLandmark(storageFormatLandmark: StoragePoint, containingLayer: PointLayerSettings): ScenePoint {
        val point = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x, storageFormatLandmark.y)

        return ScenePoint(storageFormatLandmark.uid, containingLayer, point)
    }
}
