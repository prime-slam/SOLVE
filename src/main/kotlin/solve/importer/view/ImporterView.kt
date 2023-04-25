package solve.importer.view

import solve.styles.Style
import tornadofx.View
import tornadofx.borderpane

class ImporterView : View() {
    override val root = borderpane {
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto+Condensed:wght@700")
        stylesheets.add("https://fonts.googleapis.com/css2?family=Roboto")

        style = "-fx-background-color: #${Style.surfaceColor};"
        prefHeight = 555.0
        prefWidth = 453.0
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}
