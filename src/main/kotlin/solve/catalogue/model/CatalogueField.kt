package solve.catalogue.model

import javafx.scene.image.Image
import solve.project.model.ProjectFrame
import java.lang.Double.min
import kotlin.io.path.inputStream

class CatalogueField(val frame: ProjectFrame) {
    companion object {
        private const val MaxImagePreviewHeight = 100.0
        private const val SmoothPreviewImage = false
    }

    fun loadPreviewImage(imageViewHeight: Double): Image {
        val loadingImageHeight = min(imageViewHeight, MaxImagePreviewHeight)

        return Image(
            frame.imagePath.inputStream(),
            loadingImageHeight,
            loadingImageHeight,
            true,
            SmoothPreviewImage
        )
    }

    val fileName: String
        get() = frame.imagePath.fileName.toString()
}
