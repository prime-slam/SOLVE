package solve.importer.model

data class ProjectAfterPartialParsing (val currentDirectory: String, val projectFrames: List<FrameAfterPartialParsing>, val hasAnyErrors: Boolean = false)