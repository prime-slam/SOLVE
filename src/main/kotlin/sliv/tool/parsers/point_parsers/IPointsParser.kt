package sliv.tool.parsers.point_parsers

import sliv.tool.data_structures.landmarks.PointLandmark

interface IPointsParser {
    fun parse(filePath: String): List<PointLandmark>
}