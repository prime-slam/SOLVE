package solve.utils

import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.stage.Modality
import javafx.stage.Window
import solve.importer.view.AlertDialog

fun createAlertForError(content: String, owner: Window) {
    var container = AlertDialog(content)

        val dialogContent = MFXGenericDialogBuilder.build()
            .setContent(container.root)
            .setShowMinimize(false)
            .setShowAlwaysOnTop(false)
            .setShowClose(false)
            .get()

        val dialog = MFXGenericDialogBuilder.build(dialogContent)
            .toStageDialogBuilder()
            .initOwner(owner)
            .initModality(Modality.APPLICATION_MODAL)
            .setDraggable(true)
            .setScrimPriority(ScrimPriority.WINDOW)
            .setScrimOwner(true)
            .get()

       dialog.show()
}
fun List<String>.toStringWithoutBrackets(): String {
    return this.toString().replace("[", "").replace("]", "")
}