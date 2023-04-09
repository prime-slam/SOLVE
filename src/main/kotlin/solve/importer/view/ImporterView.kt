package solve.importer.view

import io.github.palexdev.materialfx.css.themes.MFXThemeManager
import io.github.palexdev.materialfx.css.themes.Themes
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import solve.importer.controller.ImporterController
import tornadofx.View
import tornadofx.borderpane

class ImporterView : View() {

    val importer = find<DirectoryPathView>()

    private val directoryChooser = DirectoryChooser().apply { title = "Choose working directory" }

    private val controller: ImporterController by inject()

    override fun onDock() {
        (root.scene.window as Stage).minWidth = 390.0
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        MFXThemeManager.addOn(root.scene, Themes.DEFAULT)
    }

    override val root = borderpane {
        style="-fx-background-color: #ffffff;"
        prefHeight = 570.0
        prefWidth = 453.0
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}