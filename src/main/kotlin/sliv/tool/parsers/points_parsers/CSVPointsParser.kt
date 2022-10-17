package sliv.tool.parsers.points_parsers

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import sliv.tool.data_structures.landmarks.Point
import sliv.tool.data_structures.landmarks.PointLandmark
import sliv.tool.parsers.ParserUtils

class CSVPointsParser : IPointsParser {
    private data class CSVParserPointLandmark(val uid: Long, val x: Double, val y: Double)

    private val csvMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(filePath: String): List<PointLandmark> {
        val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

        val pointLandmarks = mutableListOf<PointLandmark>()

        val csvSchema =
            csvMapper.readerFor(CSVParserPointLandmark::class.java).with(CsvSchema.emptySchema().withHeader())
        val mappingIterator = csvSchema.readValues<CSVParserPointLandmark>(textData)

        while (mappingIterator.hasNextValue()) {
            val csvParserPointLandmark: CSVParserPointLandmark = mappingIterator.nextValue()
            val pointLandmark = PointLandmark(
                csvParserPointLandmark.uid,
                Point(csvParserPointLandmark.x, csvParserPointLandmark.y),
            )
            pointLandmarks.add(pointLandmark)
        }

        return pointLandmarks
    }
}
