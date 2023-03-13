package solve.importer.view

import javafx.stage.Stage
import solve.styles.DarkTheme
import solve.styles.LightTheme
import tornadofx.*

class ImporterView : View("Choose working directory") {
    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override val root = borderpane {
        addClass(DarkTheme.backgroundElement)
        addClass(LightTheme.importerBackground)
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}