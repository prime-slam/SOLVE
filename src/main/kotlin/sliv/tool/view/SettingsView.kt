package sliv.tool.view

import sliv.tool.controller.VisualizationController
import tornadofx.*

class SettingsView : View() {
    private val controller : VisualizationController by inject()

    override val root = vbox {
        label("Choose color for landmarks")
        combobox {
            items = controller.availableColors
            bindSelected(controller.landmarksColorProperty)
        }
    }
}