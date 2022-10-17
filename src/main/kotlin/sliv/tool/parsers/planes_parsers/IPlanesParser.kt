package sliv.tool.parsers.planes_parsers

import sliv.tool.data_structures.landmarks.PointLandmark

interface IPlanesParser {
    fun parse(filePath: String): List<PointLandmark>
}