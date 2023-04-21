package solve.utils

<<<<<<< HEAD
import solve.importer.view.AlertDialog
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.stage.Modality
import javafx.stage.Window
=======
import solve.importer.view.*
import tornadofx.FX.Companion.find
>>>>>>> 8e28c04 (added panels)

fun createAlertForError(content: String) {
    val container = MaterialFXDialog.createGenericDialog(AlertDialog(content).root)

<<<<<<< HEAD
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
=======
    val dialog = MaterialFXDialog.createStageDialog(container, find<DirectoryPathView>().currentStage, find<DirectoryPathView>().root ).apply {
        isDraggable = false
    }
>>>>>>> 8e28c04 (added panels)

    dialog.show()
}

fun List<String>.toStringWithoutBrackets(): String {
    return this.toString().replace("[", "").replace("]", "")
}