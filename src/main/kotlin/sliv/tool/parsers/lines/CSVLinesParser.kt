package sliv.tool.parsers.lines

import sliv.tool.models.landmarks.csv.CSVLineLandmark
import sliv.tool.parsers.CSVParser

object CSVLinesParser : CSVParser<CSVLineLandmark>(CSVLineLandmark::class.java)
