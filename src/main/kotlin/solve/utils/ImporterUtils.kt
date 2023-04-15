package solve.utils

import solve.importer.view.AlertDialog
import solve.importer.view.DirectoryPathView
import tornadofx.FX.Companion.find

fun createAlertForError(content: String) {
    val container = MaterialFXDialog.createGenericDialog(AlertDialog(content).root)

    val dialog = MaterialFXDialog.createStageDialog(
        container,
        find<DirectoryPathView>().currentStage,
        find<DirectoryPathView>().root
    ).apply {
        isDraggable = false
    }

    dialog.show()
}

fun List<String>.toStringWithoutBrackets(): String {
    return this.toString().replace("[", "").replace("]", "")
}