package sliv.tool.catalogue.model

import javafx.scene.image.Image
import sliv.tool.project.model.ProjectFrame
import kotlin.io.path.inputStream

class CatalogueField(val frame: ProjectFrame) {
    val imagePreview: Image
        get() = Image(frame.imagePath.inputStream())

    val fileName: String
        get() = frame.imagePath.fileName.toString()
}
