package sliv.tool.parsers.factories

import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer.LineLayer
import sliv.tool.parsers.structures.Line as StorageLine
import sliv.tool.scene.model.Landmark.Line as SceneLine

object LineFactory : LandmarkFactory<StorageLine, SceneLine, LineLayer> {
    override fun buildLandmark(storageFormatLandmark: StorageLine, containingLayer: LineLayer): SceneLine {
        val startPoint =
            ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x0, storageFormatLandmark.y0)
        val endPoint =
            ParserUtils.doubleCoordinatesToScenePoint(storageFormatLandmark.x1, storageFormatLandmark.y1)

        return SceneLine(storageFormatLandmark.uid, containingLayer, startPoint, endPoint)
    }
}
