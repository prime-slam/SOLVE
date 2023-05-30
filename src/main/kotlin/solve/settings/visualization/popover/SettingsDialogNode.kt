package solve.settings.visualization.popover

import javafx.scene.layout.VBox
import solve.styles.Style
import tornadofx.*

class SettingsDialogNode : VBox(10.0) {

    var title: String = ""

    fun addTitle() {
        val titleLabel = label(title) {
            paddingLeft = 10.0
            style = "-fx-font-family: ${Style.FontCondensed}; -fx-font-size: ${Style.SettingsDialogFontSize};"
        }

        this.add(titleLabel)
    }
}
