package solve.parsers.lines

import solve.parsers.CSVParser
import solve.parsers.structures.Line

object CSVLinesParser : CSVParser<Line>(Line::class.java)
