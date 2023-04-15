package solve.utils

import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.scene.control.Alert
import javafx.stage.Modality
import javafx.stage.Window
import solve.importer.view.AlertDialog

//fun createAlertForError(content: String, owner: Window) {
//    val alert = Alert(Alert.AlertType.ERROR, content).apply {
//        headerText = ""
//        initModality(Modality.APPLICATION_MODAL)
//        initOwner(owner)
//    }
//    alert.show()
//}


fun createAlertForError(content: String, owner: Window) {

    var container = AlertDialog(content)


//    fun createGenericDialog() : MFXGenericDialog {
//        val container = ImporterView().root
//        val dialogContent = MFXGenericDialogBuilder.build()
//            .setContentText(content)
//            .setShowMinimize(false)
//            .setShowAlwaysOnTop(false)
//            .setShowClose(false)
//            .get()
//
//        return dialogContent
//
//    }

//    fun createStageDialog(dialogContent: MFXGenericDialog) : MFXStageDialog {

        val dialogContent = MFXGenericDialogBuilder.build()
            .setContent(container.root)
//            .setContentText(content)
            .setShowMinimize(false)
            .setShowAlwaysOnTop(false)
            .setShowClose(false)
            .get()


        val dialog = MFXGenericDialogBuilder.build(dialogContent)
            .toStageDialogBuilder()
//            .setAlwaysOnTop(true)
            .initOwner(owner)
            .initModality(Modality.APPLICATION_MODAL)
            .setDraggable(true)
//            .setOwnerNode(root.getScene().getRoot() as BorderPane)
            .setScrimPriority(ScrimPriority.WINDOW)
            .setScrimOwner(true)
            .get()

       dialog.show()

//        dialog.content.padding = Insets(0.0,0.0,0.0,0.0)

//    }

    val alert = Alert(Alert.AlertType.ERROR, content).apply {
        headerText = ""
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
    }
//    alert.show()
}
fun List<String>.toStringWithoutBrackets(): String {
    return this.toString().replace("[", "").replace("]", "")
}
