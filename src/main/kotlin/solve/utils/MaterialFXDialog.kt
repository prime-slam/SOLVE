package solve.utils

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage

object MaterialFXDialog {
    fun createGenericDialog(container: Pane): MFXGenericDialog {
        return MFXGenericDialogBuilder.build()
            .setContent(container)
            .setShowMinimize(false)
            .setShowAlwaysOnTop(false)
            .setShowClose(false)
            .get()
    }

    fun createStageDialog(dialogContent: MFXGenericDialog, owner: Stage?, ownerNode: Pane): MFXStageDialog {
        return MFXGenericDialogBuilder.build(dialogContent)
            .toStageDialogBuilder()
            .initOwner(owner)
            .initModality(Modality.APPLICATION_MODAL)
            .setDraggable(true)
            .setOwnerNode(ownerNode)
            .setScrimPriority(ScrimPriority.NODE)
            .setScrimOwner(true)
            .get()
    }

    fun changeContent(dialog: MFXGenericDialog, newContent: Pane) {
        dialog.content = newContent
    }
}