package sliv.tool.importer.view

import javafx.geometry.Insets
import tornadofx.borderpane
import tornadofx.View

class ImporterView: View("Choose working directory") {
    override val root = borderpane {
        prefWidth = 500.0
        prefHeight = 600.0
        padding = Insets(5.0, 0.0, 0.0, 20.0)
        top<ChoosingDirectoryView>()
    }
}