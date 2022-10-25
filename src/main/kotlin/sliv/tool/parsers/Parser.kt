package sliv.tool.parsers

// An interface for all parsers.
// Accepts T parameter as a data class corresponding to the data storage format.
interface Parser<T> {
    fun parse(filePath: String): List<T>
}