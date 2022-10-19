package sliv.tool.settings.view

import sliv.tool.scene.controller.SceneController
import tornadofx.*

class SettingsView : View() {
    private val controller: SceneController by inject()

    override val root = vbox {
        label("Choose color for landmarks")
    }
}