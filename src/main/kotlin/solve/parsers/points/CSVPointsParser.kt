package solve.parsers.points

import solve.parsers.CSVParser
import solve.parsers.structures.Point

object CSVPointsParser : CSVParser<Point>(Point::class.java)
