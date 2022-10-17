package sliv.tool.parsers.lines_parsers

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import sliv.tool.data_structures.landmarks.LineLandmark
import sliv.tool.data_structures.landmarks.Point
import sliv.tool.parsers.ParserUtils
import kotlin.system.measureTimeMillis

class CSVLinesParser : ILinesParser {
    private data class CSVLineLandmark(val uid: Long, val x0: Double, val y0: Double, val x1: Double, val y1: Double)

    private val csvMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(filePath: String): List<LineLandmark> {
        val lineLandmarks = mutableListOf<LineLandmark>()

        val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

        val csvSchema = csvMapper.readerFor(CSVLineLandmark::class.java).with(CsvSchema.emptySchema().withHeader())
        val mappingIterator = csvSchema.readValues<CSVLineLandmark>(textData)

        while (mappingIterator.hasNextValue()) {
            val csvLineLandmark: CSVLineLandmark = mappingIterator.nextValue()
            val lineLandmark = LineLandmark(
                csvLineLandmark.uid,
                Point(csvLineLandmark.x0, csvLineLandmark.y0),
                Point(csvLineLandmark.y0, csvLineLandmark.y1)
            )
            lineLandmarks.add(lineLandmark)
        }

        return lineLandmarks
    }
}
