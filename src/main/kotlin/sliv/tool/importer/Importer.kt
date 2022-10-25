package sliv.tool.importer

import sliv.tool.parsers.Parser
import sliv.tool.scene.model.Landmark as SceneLandmark

// An interface for all importers who build scene landmarks from the data received after parsing.
// Accepts T parameter as a scene landmark class.
// Accepts S parameter as a data class corresponding to the data storage format.
interface Importer<T : SceneLandmark, S> {
  fun import(filePath: String, parser: Parser<S>): List<T>
}
