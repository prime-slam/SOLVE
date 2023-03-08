package solve.utils

import javafx.scene.control.Alert
import javafx.stage.Modality
import javafx.stage.Window

fun createAlertForError(content: String, owner: Window) {
    val alert = Alert(Alert.AlertType.ERROR, content).apply {
        headerText = ""
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
    }
    alert.show()
}

fun MutableList<String>.toStringWithoutBrackets(): String {
    return this.toString().replace("[", "").replace("]", "")
}
