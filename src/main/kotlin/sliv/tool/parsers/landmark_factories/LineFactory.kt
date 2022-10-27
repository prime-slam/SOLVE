package sliv.tool.parsers.landmark_factories

import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer
import sliv.tool.parsers.structures.Line as StorageLine
import sliv.tool.scene.model.Landmark.Line as SceneLine

object LineFactory : LandmarkFactory<StorageLine, SceneLine> {
    override fun buildLandmark(storageFormatLandmark: StorageLine, containingLayer: Layer): SceneLine {
        val startPoint = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x0, storageFormatLandmark.y0)
        val endPoint = ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x1, storageFormatLandmark.y1)

        return SceneLine(storageFormatLandmark.uid, containingLayer, startPoint, endPoint)
    }
}
