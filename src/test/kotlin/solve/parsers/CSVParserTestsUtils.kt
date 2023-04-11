package solve.parsers.lines

import java.io.File

internal fun createCSVFileWithData(tempFolder: File, csvStringData: String): File {
    val csvDataFile = File(tempFolder, "data.csv")
    csvDataFile.writeText(csvStringData)

    return csvDataFile
}

internal val doubleWithCommaRegex = Regex("[+|-]?([0-9]+)\\.[0-9]* *,")

internal val lineBreakWithIntWithCommaRegex = Regex("\n[0-9]+ *,")
