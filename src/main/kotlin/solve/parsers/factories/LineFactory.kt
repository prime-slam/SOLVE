package solve.parsers.factories

import solve.parsers.ParserUtils
import solve.scene.model.LayerSettings.LineLayerSettings
import solve.scene.model.LayerState
import solve.parsers.structures.Line as StorageLine
import solve.scene.model.Landmark.Line as SceneLine

object LineFactory : LandmarkFactory<StorageLine, SceneLine, LineLayerSettings> {
    override fun buildLandmark(
        storageFormatLandmark: StorageLine, containingLayerSettings: LineLayerSettings, containingLayerState: LayerState
    ): SceneLine {
        val startPoint = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x0, storageFormatLandmark.y0)
        val endPoint = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x1, storageFormatLandmark.y1)

        return SceneLine(storageFormatLandmark.uid, containingLayerSettings, containingLayerState, startPoint, endPoint)
    }
}
