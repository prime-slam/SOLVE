package sliv.tool.parsers.points

import sliv.tool.parsers.CSVParser
import sliv.tool.parsers.structures.Point as StorageFormatPoint

object CSVPointsParser : CSVParser<StorageFormatPoint>(StorageFormatPoint::class.java)
