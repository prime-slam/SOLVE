package solve.importer.view

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.dialogs.MFXStageDialog
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.stage.Modality
import solve.main.MainView
import tornadofx.FX.Companion.find

class MaterialFXDialog {
    private var dialog = MFXStageDialog()

    private var dialogContent = MFXGenericDialog()

    fun createGenericDialog() : MFXGenericDialog {
        val container = ImporterView().root
        dialogContent = MFXGenericDialogBuilder.build()
            .setContent(container)
            .setShowMinimize(false)
            .setShowAlwaysOnTop(false)
            .setShowClose(false)
            .get()

        return dialogContent
    }

    fun createStageDialog(dialogContent: MFXGenericDialog) : MFXStageDialog {
        dialog = MFXGenericDialogBuilder.build(dialogContent)
            .toStageDialogBuilder()
            .initOwner(find<MainView>().currentStage)
            .initModality(Modality.APPLICATION_MODAL)
            .setDraggable(true)
            .setOwnerNode(find<MainView>().root)
            .setScrimPriority(ScrimPriority.NODE)
            .setScrimOwner(true)
            .get()
        return  dialog
    }

    fun changeContent(dialog: MFXGenericDialog) {
        val container = LoadingScreen().root
        dialog.content = container
    }

}