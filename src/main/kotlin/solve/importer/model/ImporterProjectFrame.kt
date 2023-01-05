package solve.importer.model

import solve.project.model.ProjectFrame

data class ImporterProjectFrame(
    val frame: ProjectFrame, var isImageErrored: Boolean = false,
    var isOutputErrored: Boolean = false,
    var errorMessage: MutableList<String>? = mutableListOf()
)