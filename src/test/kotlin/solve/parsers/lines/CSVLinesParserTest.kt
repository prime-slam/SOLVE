package solve.parsers.lines

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import solve.parsers.structures.Line
import java.io.File

internal class CSVLinesParserTest {
    @Test
    fun `Parsing a lines CSV file containing data in standard format`(@TempDir tempFolder: File) {
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
    fun `Extracting a UIDs from lines CSV file with standard format`(@TempDir tempFolder: File) {
        val uids = testLines.map { it.uid }
        val csvDataFile = createCSVFileWithData(tempFolder, csvTestLinesStringData)

        assertEquals(uids, CSVLinesParser.extractUIDs(csvDataFile.path))
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

        private fun Line.getCSVDataString() = "$uid,$x0,$y0,$x1,$y1"
    }
}
