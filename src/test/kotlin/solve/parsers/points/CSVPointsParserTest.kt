package solve.parsers.points

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.dataformat.csv.CsvReadException
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
    fun `Parsing an empty CSV file with points parser`(@TempDir tempFolder: File) {
        val emptyCSVFile = createCSVFileWithData(tempFolder, "")

        assertThrows(CsvReadException::class.java) {
            CSVPointsParser.parse(emptyCSVFile.path)
        }
    }

    @Test
    fun `Parsing a points CSV file with missing data`(@TempDir tempFolder: File) {
        val doubleWithCommaRegex = Regex("[+|-]?([0-9]+)\\.[0-9]* *,?")

        val firstEmptyCoordinatesCount = 5
        var csvStringDataWithMissingColumns =
            csvTestPointsStringData.replaceFirst(doubleWithCommaRegex, ",")
        repeat(firstEmptyCoordinatesCount - 1) {
            csvStringDataWithMissingColumns = csvStringDataWithMissingColumns.replaceFirst(doubleWithCommaRegex, ",")
        }

        val firstEmptyUIDCount = 2
        val uidWithCommaRegex = Regex("[0-9]+ *,")
        repeat(firstEmptyUIDCount) {
            csvStringDataWithMissingColumns = csvStringDataWithMissingColumns.replaceFirst(uidWithCommaRegex, ",")
        }

        val testPointsWithMissingData = testPoints.slice(3..testPoints.lastIndex).toMutableList()
        val firstPointWithAllDataNotMissing = testPoints[2]
        val missingDataPoints = listOf(
            Point(0, 0.0, 0.0),
            Point(0, 0.0, 0.0),
            Point(firstPointWithAllDataNotMissing.uid, 0.0, firstPointWithAllDataNotMissing.y)
        )
        testPointsWithMissingData.addAll(0, missingDataPoints)

        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithMissingColumns)
        assertEquals(testPointsWithMissingData, CSVPointsParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a CSV file with wrong delimiter`(@TempDir tempFolder: File) {
        val csvStringDataWithWrongDelimiter = csvTestPointsStringData.replace(",", ";")
        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithWrongDelimiter)

        assertThrows(UnrecognizedPropertyException::class.java) {
            CSVPointsParser.parse(csvDataFile.path)
        }
    }

    @Test
    fun `Extracting a UIDs from points CSV file with standard format`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestPointsStringData)

        assertEquals(testUIDs, CSVLinesParser.extractUIDs(csvDataFile.path))
    }

    @Test
    fun `Extracting a UIDs from points CSV file with only one header`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, CSVPointDataStringPrefix)

        assertEquals(emptyList<Long>(), CSVLinesParser.extractUIDs(csvDataFile.path))
    }

    @Test
    fun `Extracting a UIDs from empty CSV file with points UIDs extractor`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, "")

        assertThrows(CsvReadException::class.java) {
            CSVLinesParser.extractUIDs(csvDataFile.path)
        }
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
            testPoints.joinToString(prefix = CSVPointDataStringPrefix, separator = "\n") { it.getCSVDataString() }
        private val testUIDs = testPoints.map { it.uid }

        private fun Point.getCSVDataString() = "$uid,$x,$y"
    }
}
