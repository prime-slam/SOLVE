package solve.utils

import javafx.scene.control.Alert
import javafx.stage.Modality
import javafx.stage.Window

fun createAlert(content: String, owner: Window) {
    val alert = Alert(Alert.AlertType.ERROR, content).apply {
        headerText = ""
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
    }
    alert.show()
}