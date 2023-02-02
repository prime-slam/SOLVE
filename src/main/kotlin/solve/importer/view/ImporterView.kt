package solve.importer.view

import javafx.geometry.Insets
import javafx.stage.Stage
import tornadofx.borderpane
import tornadofx.View

class ImporterView : View("Choose working directory") {
    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root = borderpane {
        padding = Insets(8.0, 10.0, 8.0, 10.0)
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}