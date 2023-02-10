package solve.settings.visualization

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Dialog
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import solve.settings.visualization.dialog.PointLayerSettingsDialogNode
import solve.utils.*
import tornadofx.*

class VisualizationSettingsView: View() {
    companion object {
        private const val LayerEditIconSize = 14.0
        private const val LayerEditFieldNameFontSize = 16.0
    }

    private val pointLayerSettingsDialogNode: PointLayerSettingsDialogNode = find()
    private val pointLayerSettingsDialog = createSettingsDialog(pointLayerSettingsDialogNode)

    private val editIconImage = loadImage("icons/visualization_settings_edit_icon.png")

    override val root = vbox {
        vbox {
            vbox(5) {
                addStylesheet(VisualizationSettingsStyle::class)

                add(createLayerEditField("Keypoints") { pointLayerSettingsDialog.showAndWait() })
                separator()
                add(createLayerEditField("Lines") { }) // TODO: add a line landmarks settings panel.
                separator()
                add(createLayerEditField("Planes") { }) // TODO: add a plane landmarks settings panel.

                padding = createInsetsWithValue(5.0)
                usePrefSize = true
            }
            border =
                Border(BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT))
            hgrow = Priority.ALWAYS
            padding = createInsetsWithValue(3.0)
        }
        padding = createInsetsWithValue(5.0)
    }

    private inline fun createLayerEditField(name: String, crossinline action: () -> Unit) = hbox(3) {
        label(name) {
            font = Font.font(LayerEditFieldNameFontSize)
        }
        add(createHGrowHBox())
        button {
            editIconImage ?: return@button
            graphic = createImageViewIcon(editIconImage, LayerEditIconSize)
            alignment = Pos.CENTER_RIGHT

            action { action() }
        }
    }

    private fun createSettingsDialog(dialogNode: View): Dialog<Int>
    {
        val dialog = Dialog<Int>() // A dialog type does not matter (not typed dialog has another view).
        val dialogWindow = dialog.dialogPane.scene.window
        dialogWindow.setOnCloseRequest {
            dialogWindow.hide()
        }
        (dialogWindow as Stage).initModality(Modality.APPLICATION_MODAL)
        dialog.dialogPane.content = dialogNode.root

        Platform.runLater {
            dialog.initOwner(currentWindow)
            // Make an invisible dialog icon.
            (dialogWindow as Stage).icons.add(createFakeTransparentImage(100, 100))
        }

        return dialog
    }
}

class VisualizationSettingsStyle: Stylesheet() {
    companion object {
        private val LayerEditButtonBackgroundColor = Color.TRANSPARENT
        private const val HoveredEditButtonScale = 1.4
    }
    init {
        button {
            backgroundColor += LayerEditButtonBackgroundColor

            and(hover) {
                scale(HoveredEditButtonScale)
            }
        }
    }
}
