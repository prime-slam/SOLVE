package solve.importer.view

import solve.styles.Style
import solve.styles.TooltipStyle
import tornadofx.*

class ImporterView : View() {
    override val root = borderpane {
        addStylesheet(TooltipStyle::class)

        style = "-fx-background-color: #${Style.SurfaceColor};"

        prefHeight = 555.0
        prefWidth = 453.0
        top<DirectoryPathView>()
        center<ProjectTreeView>()
        bottom<ControlPanel>()
    }
}
