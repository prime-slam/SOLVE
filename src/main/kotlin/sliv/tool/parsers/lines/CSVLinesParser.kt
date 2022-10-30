package sliv.tool.parsers.lines

import sliv.tool.parsers.CSVParser
import sliv.tool.parsers.structures.Line

object CSVLinesParser : CSVParser<Line>(Line::class.java)
