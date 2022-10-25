package sliv.tool.parsers.lines

import sliv.tool.parsers.structures.Line
import sliv.tool.parsers.CSVParser

object CSVLinesParser : CSVParser<Line>(Line::class.java)
