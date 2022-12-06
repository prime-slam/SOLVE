package solve.catalogue.model

import javafx.scene.image.Image
import solve.project.model.ProjectFrame
import kotlin.io.path.inputStream

class CatalogueField(val frame: ProjectFrame) {
    val imagePreview: Image
        get() = Image(frame.imagePath.inputStream())

    val fileName: String
        get() = frame.imagePath.fileName.toString()
}
