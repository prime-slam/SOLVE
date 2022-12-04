package sliv.tool.importer.view

import javafx.geometry.Insets
import tornadofx.borderpane
import tornadofx.View

class ImporterView : View("Choose working directory") {
    override val root = borderpane {
        padding = Insets(8.0, 10.0, 8.0, 10.0)
        top<ChoosingDirectoryView>()
    }
}