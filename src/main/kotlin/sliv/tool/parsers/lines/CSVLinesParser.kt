package sliv.tool.parsers.lines

import sliv.tool.parsers.CSVParser
import sliv.tool.parsers.structures.Line as StorageFormatLine

object CSVLinesParser : CSVParser<StorageFormatLine>(StorageFormatLine::class.java)
