package solve.parsers.lines

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.dataformat.csv.CsvReadException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import solve.parsers.structures.Line
import java.io.File

internal class CSVLinesParserTest {
    @Test
    fun `Parsing a lines CSV file containing data in a standard format`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestLinesStringData)

        assertEquals(testLines, CSVLinesParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a lines CSV file containing data with modified and additional symbols`(@TempDir tempFolder: File) {
        var modifiedCSVStringData = csvTestLinesStringData.replace(",", ", ")
        modifiedCSVStringData = modifiedCSVStringData.replace("\n", ",\n")
        modifiedCSVStringData += "\n"

        val csvDataFile = createCSVFileWithData(tempFolder, modifiedCSVStringData)

        assertEquals(testLines, CSVLinesParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a lines CSV file with only one header`(@TempDir tempFolder: File) {
        val csvStringDataWithoutLines = CSVLineDataStringPrefix
        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithoutLines)

        assertEquals(emptyList<Line>(), CSVLinesParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing an empty CSV file with a lines parser`(@TempDir tempFolder: File) {
        val emptyCSVFile = createCSVFileWithData(tempFolder, "")

        assertThrows(CsvReadException::class.java) {
            CSVLinesParser.parse(emptyCSVFile.path)
        }
    }

    @Test
    fun `Parsing a lines CSV file with missing data values`(@TempDir tempFolder: File) {
        val firstEmptyCoordinatesCount = 8
        var csvStringDataWithMissingValues = csvTestLinesStringData.replaceFirst(doubleWithCommaRegex, ",")
        repeat(firstEmptyCoordinatesCount - 1) {
            csvStringDataWithMissingValues = csvStringDataWithMissingValues.replaceFirst(doubleWithCommaRegex, ",")
        }

        val firstEmptyUIDsCount = 2
        repeat(firstEmptyUIDsCount) {
            csvStringDataWithMissingValues =
                csvStringDataWithMissingValues.replaceFirst(lineBreakWithIntWithCommaRegex, "\n,")
        }

        println(csvStringDataWithMissingValues)

        val testLinesWithMissingData = testLines.slice(3..testLines.lastIndex).toMutableList()
        val firstLineWithAllDataNotMissing = testLines[2]
        val missingLinesData = listOf(
            Line(0, 0.0, 0.0, 0.0, 2.0),
            Line(0, 0.0, 0.0, 0.0, 3.0),
            Line(3, 0.0, 0.0, firstLineWithAllDataNotMissing.x1, firstLineWithAllDataNotMissing.y1)
        )
        testLinesWithMissingData.addAll(0, missingLinesData)

        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithMissingValues)
        assertEquals(testLinesWithMissingData, CSVLinesParser.parse(csvDataFile.path))
    }

    @Test
    fun `Parsing a lines CSV file with a wrong delimiter`(@TempDir tempFolder: File) {
        val csvStringDataWithWrongDelimiter = csvTestLinesStringData.replace(",", ";")
        val csvDataFile = createCSVFileWithData(tempFolder, csvStringDataWithWrongDelimiter)

        assertThrows(UnrecognizedPropertyException::class.java) {
            CSVLinesParser.parse(csvDataFile.path)
        }
    }

    @Test
    fun `Extracting UIDs from lines CSV file with a standard format`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestLinesStringData)

        assertEquals(testUIDs, CSVLinesParser.extractUIDs(csvDataFile.path))
    }

    @Test
    fun `Extracting UIDs from lines CSV file with only one header`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, CSVLineDataStringPrefix)

        assertEquals(emptyList<Long>(), CSVLinesParser.extractUIDs(csvDataFile.path))
    }

    @Test
    fun `Extracting UIDs from empty CSV file with lines extractor`(@TempDir tempFolder: File) {
        val csvDataFile = createCSVFileWithData(tempFolder, "")

        assertThrows(CsvReadException::class.java) {
            CSVLinesParser.extractUIDs(csvDataFile.path)
        }
    }

    companion object {
        @TempDir
        lateinit var tempFolder: File

        private const val CSVLineDataStringPrefix = "uid,x0,y0,x1,y1\n"

        private val testLines = listOf(
            Line(1, 1.0, 1.0, 2.0, 2.0),
            Line(2, -1.5, 2.0, -2.5, 3.0),
            Line(3, 0.0, 0.0, 0.0, 0.0),
            Line(5, -4.0, -10.0, -5.0, -4.0),
            Line(4, -9.0, 5.0, 7.0, 10.0),
            Line(8, 7.123, 14.12, 10.4, 25.9),
            Line(9, 1.0, 8.0, 32.0, 12.0),
        )
        private val csvTestLinesStringData =
            testLines.joinToString(prefix = CSVLineDataStringPrefix, separator = "\n") { it.getCSVDataString() }
        private val testUIDs = testLines.map { it.uid }

        private fun Line.getCSVDataString() = "$uid,$x0,$y0,$x1,$y1"
    }
}
