package sliv.tool.parsers.points

import sliv.tool.parsers.structures.Point
import sliv.tool.parsers.CSVParser

object CSVPointsParser : CSVParser<Point>(Point::class.java)
