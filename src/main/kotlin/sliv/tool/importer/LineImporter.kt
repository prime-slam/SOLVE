package sliv.tool.importer

import sliv.tool.parsers.Parser
import sliv.tool.parsers.ParserUtils
import sliv.tool.scene.model.Layer.LineLayer
import sliv.tool.parsers.structures.Line as ParserLine
import sliv.tool.scene.model.Landmark.Line as SceneLine

object LineImporter : Importer<SceneLine, ParserLine> {
  private fun buildLine(parserLine: ParserLine): SceneLine {
    val layer = LineLayer(parserLine.uid.toString())
    val startPoint = ParserUtils.doubleCoordinatesToScenePoint(parserLine.x0, parserLine.y0)
    val endPoint = ParserUtils.doubleCoordinatesToScenePoint(parserLine.x1, parserLine.y1)
    return SceneLine(parserLine.uid, layer, startPoint, endPoint)
  }
  override fun import(filePath: String, parser: Parser<ParserLine>): List<SceneLine> {
    val lineLandmarks: List<ParserLine> = parser.parse(filePath)
    return lineLandmarks.map { buildLine(it) }
  }
}
