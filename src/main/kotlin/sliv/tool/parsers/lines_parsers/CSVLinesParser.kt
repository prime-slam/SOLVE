package sliv.tool.parsers.lines_parsers

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import sliv.tool.data_structures.landmarks.LineLandmark
import sliv.tool.data_structures.landmarks.Point
import sliv.tool.parsers.ParserUtils

class CSVLinesParser : ILinesParser {
    private data class CSVParserLineLandmark(
        val uid: Long,
        val x0: Double,
        val y0: Double,
        val x1: Double,
        val y1: Double
    )

    private val csvMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(filePath: String): List<LineLandmark> {
        val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

        val lineLandmarks = mutableListOf<LineLandmark>()

        val csvSchema =
            csvMapper.readerFor(CSVParserLineLandmark::class.java).with(CsvSchema.emptySchema().withHeader())
        val mappingIterator = csvSchema.readValues<CSVParserLineLandmark>(textData)

        while (mappingIterator.hasNextValue()) {
            val csvParserLineLandmark: CSVParserLineLandmark = mappingIterator.nextValue()
            val lineLandmark = LineLandmark(
                csvParserLineLandmark.uid,
                Point(csvParserLineLandmark.x0, csvParserLineLandmark.y0),
                Point(csvParserLineLandmark.y0, csvParserLineLandmark.y1)
            )
            lineLandmarks.add(lineLandmark)
        }

        return lineLandmarks
    }
}
