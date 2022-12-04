package solve.catalogue.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import solve.project.model.ProjectFrame

enum class ViewFormat {
    FileName,
    ImagePreview;

    fun differentFormat() = when (this) {
        FileName -> ImagePreview
        ImagePreview -> FileName
    }
}

class CatalogueModel {
    private val _frames = FXCollections.observableArrayList<ProjectFrame>()
    val frames: ObservableList<ProjectFrame> = FXCollections.unmodifiableObservableList(_frames)

    fun reinitializeFrames(newFrames: List<ProjectFrame>) {
        _frames.clear()
        _frames.addAll(newFrames)
    }
}
