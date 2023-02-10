package solve.settings.visualization

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import solve.scene.controller.SceneController
import solve.utils.*
import tornadofx.*

class PointLayerSettingsDialogNode: View() {
    companion object {
        private const val LayerSettingNameFontSize = 16.0

        private const val LayerSettingsWindowWidth = 250.0
        private const val LayerSettingsWindowHeight = 50.0

        private const val LayerSettingsWindowMinWidth = 250.0
        private const val LayerSettingsWindowMinHeight = 50.0
    }

    private val sceneController: SceneController by inject()

    private val pointLandmarksColorPicker = colorpicker {
        setOnAction {
            sceneController.setPointLayersColor(value)
        }
        sceneController.scene.onChange {
            value = sceneController.getPointLayersColor()
        }
    }

    init {
        Platform.runLater {
            currentStage?.scene?.fill = Color.TRANSPARENT
            currentStage?.scene?.root?.effect = DropShadow()
        }
    }

    override val root = vbox {
        setPrefSize(LayerSettingsWindowWidth, LayerSettingsWindowHeight)

        add(createSettingField("Color", pointLandmarksColorPicker))

        padding = createInsetsWithValue(10.0)
        Platform.runLater {
            setWindowMinSize(LayerSettingsWindowMinWidth, LayerSettingsWindowMinHeight)
        }
    }

    private fun createSettingField(name: String, settingNode: Node) = hbox(3) {
        label(name) {
            font = Font.font(LayerSettingNameFontSize)
        }
        add(createHGrowHBox())
        add(settingNode)
    }
}
