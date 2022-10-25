package sliv.tool.importer

import sliv.tool.parsers.Parser
import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer.LineLayer
import sliv.tool.parsers.structures.Point as ParserPoint
import sliv.tool.scene.model.Landmark.Keypoint as ScenePoint

object PointImporter : Importer<ScenePoint, ParserPoint> {
  private fun buildPoint(parserPoint: ParserPoint): ScenePoint {
    val layer = LineLayer(parserPoint.uid.toString())
    val point = ParserUtils.doubleCoordinatesToScenePoint(parserPoint.x, parserPoint.y)
    return ScenePoint(parserPoint.uid, layer, point)
  }
  override fun import(filePath: String, parser: Parser<ParserPoint>): List<ScenePoint> {
    val lineLandmarks: List<ParserPoint> = parser.parse(filePath)
    return lineLandmarks.map { buildPoint(it) }
  }
}
