package sliv.tool.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule

// A base class for CSV parsers.
abstract class CSVParser<T>(private val format: Class<T>) : Parser<T> {
    protected val mapper: ObjectMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(filePath: String): List<T> {
        val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

        val csvSchema = mapper.readerFor(format).with(CsvSchema.emptySchema().withHeader())
        return csvSchema.readValues<T>(textData).readAll()
    }

    override fun extractUIDs(filePath: String): List<Long> {
        val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

        val uids = mutableListOf<Long>()
        val csvSchema = mapper.readerFor(Map::class.java).with(CsvSchema.emptySchema().withHeader())
        val mappingIterator = csvSchema.readValues<Map<String, Any>>(textData)
        mappingIterator.forEach { uids.add((it["uid"] as String).toLong()) }

        return uids
    }
}
