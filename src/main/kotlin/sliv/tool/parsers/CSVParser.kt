package sliv.tool.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule

// A base class for CSV parsers.
abstract class CSVParser<T>(private val scheme: Class<T>) : Parser<T> {
  protected val mapper: ObjectMapper = CsvMapper().registerModule(KotlinModule.Builder().build())

  override fun parse(filePath: String): List<T> {
    val textData = ParserUtils.readFileText(filePath) ?: return emptyList()

    val csvSchema = mapper.readerFor(scheme).with(CsvSchema.emptySchema().withHeader())
    return csvSchema.readValues<T>(textData).readAll()
  }
}
