package solve.project.model

data class ProjectLayer(val kind: LayerKind, val name: String, val projectName: String) {
    val key = name + "_" + projectName
}
