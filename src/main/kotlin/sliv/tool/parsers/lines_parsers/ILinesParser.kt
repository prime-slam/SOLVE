package sliv.tool.parsers.lines_parsers

import sliv.tool.data_structures.landmarks.LineLandmark

interface ILinesParser {
    fun parse(filePath: String): List<LineLandmark>
}