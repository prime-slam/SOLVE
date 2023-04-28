package solve.utils

import io.github.palexdev.materialfx.controls.MFXListView
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder
import io.github.palexdev.materialfx.dialogs.MFXStageDialogBuilder
import io.github.palexdev.materialfx.effects.DepthLevel
import io.github.palexdev.materialfx.enums.ScrimPriority
import javafx.collections.MapChangeListener
import javafx.stage.Modality
import javafx.stage.Window
import tornadofx.toObservable

class ChooserDialog<T>(
    private val title: String,
    private val width: Double,
    private val height: Double,
    owner: Window
) {
    private val stageDialog =
        MFXStageDialogBuilder.build()
            .initOwner(owner)
            .initModality(Modality.APPLICATION_MODAL)
            .setDraggable(true)
            .setCenterInOwnerNode(false)
            .setScrimPriority(ScrimPriority.WINDOW)
            .setScrimOwner(true)
            .get()

    fun choose(items: List<T>): T {
        var chosenItem: T? = null
        val chooserListView = MFXListView(items.toObservable()).also { listView ->
            listView.depthLevel = DepthLevel.LEVEL0
            listView.selectionModel.selectionProperty().addListener(
                MapChangeListener { newValue ->
                    chosenItem = newValue.valueAdded
                    stageDialog.close()
                }
            )
        }
        val dialogContent = MFXGenericDialogBuilder.build()
            .setContent(chooserListView)
            .setHeaderText(title)
            .setShowClose(false)
            .setShowAlwaysOnTop(false)
            .setShowMinimize(false)
            .get().also {
                it.setMinSize(width, height)
                it.setMaxSize(width, height)
            }

        stageDialog.content = dialogContent
        stageDialog.showAndWait()
        return chosenItem!!
    }
}
