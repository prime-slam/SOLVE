package solve.importer.model

data class ImageAfterPartialParsing(
    val name: String,
    val path: String,
    val errors: MutableList<String>
)