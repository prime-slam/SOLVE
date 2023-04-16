package solve.importer.view

import solve.styles.Style
import tornadofx.View
import tornadofx.borderpane

class ImporterView : View() {
    val importer = find<DirectoryPathView>()

    override val root = borderpane {
        style = "-fx-background-color: #${Style.surfaceColor};"
        prefHeight = 555.0
        prefWidth = 453.0
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}
