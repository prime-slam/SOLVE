package solve.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import solve.utils.readFileText

// A base class for CSV parsers.
abstract class CSVParser<T>(private val format: Class<T>) : Parser<T> {
    protected val mapper: ObjectMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(filePath: String): List<T> {
        val textData = readFileText(filePath) ?: return emptyList()

        val csvSchema = mapper.readerFor(format).with(CsvSchema.emptySchema().withHeader())
        return csvSchema.readValues<T>(textData).readAll()
    }

    override fun extractUIDs(filePath: String): List<Long> {
        val textData = readFileText(filePath) ?: return emptyList()

        val csvSchema = mapper.readerFor(Map::class.java).with(CsvSchema.emptySchema().withHeader())
        val values = csvSchema.readValues<Map<String, Any>>(textData).readAll()

        return values.map { (it["uid"] as String).toLong() }
    }
}
