package solve.importer.view

import javafx.stage.Stage
import solve.DarkTheme
import solve.LightTheme
import tornadofx.borderpane
import tornadofx.View
import tornadofx.addClass

class ImporterView : View("Choose working directory") {
    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root = borderpane {
        addClass(DarkTheme.backgroundElement)
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}