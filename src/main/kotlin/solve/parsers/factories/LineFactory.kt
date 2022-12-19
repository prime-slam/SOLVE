package solve.parsers.factories

import solve.parsers.ParserUtils
import solve.scene.model.LayerSettings.LineLayerSettings
import solve.parsers.structures.Line as StorageLine
import solve.scene.model.Landmark.Line as SceneLine

object LineFactory : LandmarkFactory<StorageLine, SceneLine, LineLayerSettings> {
    override fun buildLandmark(storageFormatLandmark: StorageLine, containingLayer: LineLayerSettings): SceneLine {
        val startPoint =
            ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x0, storageFormatLandmark.y0)
        val endPoint =
            ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x1, storageFormatLandmark.y1)

        return SceneLine(storageFormatLandmark.uid, containingLayer, startPoint, endPoint)
    }
}
