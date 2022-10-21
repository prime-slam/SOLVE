package sliv.tool.parsers

interface Parser<T> {
    fun parse(filePath: String): List<T>
}