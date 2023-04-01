package solve.importer.model

import solve.project.model.LayerKind

data class OutputAfterPartialParsing(
    val name: String,
    val path: String,
    val algorithmName: String,
    val kind: LayerKind,
    val errors: MutableList<String>
)
