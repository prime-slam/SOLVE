package sliv.tool.model.project

import java.nio.file.Path

class Project {
    var currentDirectory: Path? = null
        set(value) {
            TODO("Validate")
            updateFrames()
        }

    var frames: List<ProjectFrame>? = null

    private fun updateFrames() {
        TODO("Load project frames from the new directory")
    }
}