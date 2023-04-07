package solve.parsers.points

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import solve.parsers.lines.CSVLinesParser
import solve.parsers.lines.createCSVFileWithData
import solve.parsers.structures.Point
import java.io.File

internal class CSVPointsParserTest {
    @Test
    fun `Parsing a points CSV file containing data in standard format`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestPointsStringData)

        assertEquals(testPoints, CSVPointsParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a points CSV file containing data with modified and additional symbols`(@TempDir tempFolder: File) {
        var modifiedCSVStringData = csvTestPointsStringData.replace(",", ", ")
        modifiedCSVStringData = modifiedCSVStringData.replace("\n", ",\n")
        modifiedCSVStringData += "\n"

        val csvDataFile = createCSVFileWithData(tempFolder, modifiedCSVStringData)

        assertEquals(testPoints, CSVPointsParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a points CSV file with only one header`(@TempDir tempFolder: File) {
        val csvStringDataWithoutPoints = CSVPointDataStringPrefix
        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithoutPoints)

        assertEquals(emptyList<Point>(), CSVPointsParser.parse(csvDataFile.path))
    }

    @Test
    fun `Extracting a UIDs from points CSV file with standard format`(@TempDir tempFolder: File) {
        val uids = testPoints.map { it.uid }
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestPointsStringData)

        assertEquals(uids, CSVLinesParser.extractUIDs(csvDataFile.path))
    }

    companion object {
        @TempDir
        lateinit var tempFolder: File

        private const val CSVPointDataStringPrefix = "uid,x,y\n"

        private val testPoints = listOf(
            Point(1, 5.0, 7.0),
            Point(2, -8.5, -9.25),
            Point(3, 5.1, -7.0),
            Point(5, 2.0, 0.0),
            Point(4, 1.1, 3.0),
            Point(8, 5.0, 14.0),
            Point(9, 3.0, 5.0),
        )
        private val csvTestPointsStringData =
            testPoints.joinToString(prefix = CSVPointDataStringPrefix, separator = "\n") { it.toString() }
    }
}