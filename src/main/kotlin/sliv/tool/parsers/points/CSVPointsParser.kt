package sliv.tool.parsers.points

import sliv.tool.parsers.CSVParser
import sliv.tool.parsers.structures.Point

object CSVPointsParser : CSVParser<Point>(Point::class.java)
