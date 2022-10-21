package sliv.tool.parsers.points

import sliv.tool.models.landmarks.csv.CSVPointLandmark
import sliv.tool.parsers.CSVParser

object CSVPointsParser : CSVParser<CSVPointLandmark>(CSVPointLandmark::class.java)
