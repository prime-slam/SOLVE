package solve.importer.model

data class ProjectAfterPartialParsing (val currentDirectory: String, val value: List<FrameAfterPartialParsing>, val hasAnyErrors: Boolean = false)