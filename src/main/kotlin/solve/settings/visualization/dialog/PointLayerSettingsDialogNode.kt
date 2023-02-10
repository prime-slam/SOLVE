package solve.settings.visualization.dialog

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import solve.scene.controller.SceneController
import solve.utils.*
import tornadofx.*

class PointLayerSettingsDialogNode: LayerSettingsDialogNode() {
    private val pointLandmarksColorPicker = colorpicker {
        setOnAction {
            sceneController.setPointLayersColor(value)
        }
        sceneController.scene.onChange {
            value = sceneController.getPointLayersColor()
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
}
