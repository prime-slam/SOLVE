package solve.settings.visualization

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration
import org.controlsfx.control.PopOver
import solve.scene.controller.SceneController
import solve.scene.model.LandmarkType
import solve.scene.model.LayerSettings
import solve.settings.visualization.popover.PointLayerSettingsPopOverNode
import solve.utils.*
import tornadofx.*

class VisualizationSettingsView: View() {
    companion object {
        private const val PanelFieldsContainerChildIndex = 1

        private const val LayerEditIconSize = 14.0
        private const val LayerFieldNameFontSize = 12.0
        private const val LayerTypePanelNameFontSize = 16.0

        private const val LayerSettingsPopOverHorizontalOffset = -10.0

        private const val LayerFieldsLeftPadding = 10.0
    }

    private val editIconImage = loadImage("icons/visualization_settings_edit_icon.png")

    private val pointLayersPanel = createLayerTypePanel("Keypoints")
    private val lineLayersPanel = createLayerTypePanel("Lines")
    private val planeLayersPanel = createLayerTypePanel("Planes")

    private val sceneController: SceneController by inject()

    init {
        initializeLayersUpdating()
    }

    override val root = vbox {
        vbox {
            vbox(5) {
                addStylesheet(VisualizationSettingsStyle::class)
                add(pointLayersPanel)
                separator()
                add(lineLayersPanel) // TODO: add a line landmarks settings panel.
                separator()
                add(planeLayersPanel) // TODO: add a plane landmarks settings panel.

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

    private fun createLayerTypePanel(name: String) = vbox(3) {
        val haveLayerFields = booleanProperty(false)

        vbox(3) {
            label(name) {
                font = Font.font(LayerTypePanelNameFontSize)
            }
        }

        val fieldsContainerVBox = vbox(3) {
            paddingLeft = LayerFieldsLeftPadding
        }

        fieldsContainerVBox.children.onChange {
            haveLayerFields.value = fieldsContainerVBox.children.isNotEmpty()
        }
    }

    private fun addLayerFieldToPanel(field: Node, panel: Node) {
        panel.getChildList()?.get(PanelFieldsContainerChildIndex)?.add(field)
    }

    private fun clearLayersPanel(panel: Node) {
        panel.getChildList()?.get(PanelFieldsContainerChildIndex)?.clearChildren()
    }

    private inline fun createLayerField(name: String, crossinline action: (Label) -> Unit) = hbox(3) {
        val layerLabel = label(name) {
            font = Font.font(LayerFieldNameFontSize)
        }
        add(createHGrowHBox())
        button {
            editIconImage ?: return@button
            graphic = createImageViewIcon(editIconImage, LayerEditIconSize)
            alignment = Pos.CENTER_RIGHT

            action { action(layerLabel) }
        }
    }

    private fun createLayerSettingsPopOver(contentNode: Node, titleLabel: String): PopOver
    {
        val popOver = PopOver(contentNode)
        popOver.arrowLocation = PopOver.ArrowLocation.RIGHT_TOP
        popOver.title = titleLabel

        // Needed for the safe window closing.
        Platform.runLater {
            currentWindow?.setOnCloseRequest {
                popOver.hide(Duration.ZERO)
            }
        }

        return popOver
    }

    private fun initializeLayersUpdating() {
        sceneController.scene.onChange {
            reimportLayers()
        }
    }

    private fun reimportLayers() {
        LandmarkType.values().forEach { layersType ->
            reimportLayersWithType(layersType)
        }
    }

    private fun reimportLayersWithType(layersType: LandmarkType) {
        val layersPanel = getLayersPanelWithType(layersType)
        val layersSettings = sceneController.getLayersSettingsWithType(layersType)

        clearLayersPanel(layersPanel)
        layersSettings.forEach { settings ->
            val layerPanelNode = createLayerSettingsPopOverNode(layersType, settings) ?: return@forEach
            val layerSettingsPopOver =
                createLayerSettingsPopOver(layerPanelNode, "${layersType.name} - ${settings.name}")
            val layerField = createLayerField(layersType.name) { label ->
                layerSettingsPopOver.show(label, LayerSettingsPopOverHorizontalOffset)
            }
            addLayerFieldToPanel(layerField, layersPanel)
        }
    }

    private fun createLayerSettingsPopOverNode(layerPanelType: LandmarkType, layerSettings: LayerSettings): Node? {
        if (getLayerSettingsType(layerSettings) != layerPanelType) {
            println("Creating layer panel type does not correspond the layer settings type!")
            return null
        }

        return when (layerPanelType) {
            LandmarkType.Keypoint ->
                PointLayerSettingsPopOverNode(
                    layerSettings as LayerSettings.PointLayerSettings,
                    sceneController
                ).getPopOverNode()
            LandmarkType.Line -> null // TODO: add a line layers panel realization.
            LandmarkType.Plane -> null // TODO: add a plane layers panel realization.
        }
    }

    private fun getLayerSettingsType(layerSettings: LayerSettings) = when (layerSettings) {
        is LayerSettings.PointLayerSettings -> LandmarkType.Keypoint
        is LayerSettings.LineLayerSettings -> LandmarkType.Line
        is LayerSettings.PlaneLayerSettings -> LandmarkType.Plane
    }

    private fun getLayersPanelWithType(layersPanelType: LandmarkType) = when(layersPanelType) {
        LandmarkType.Keypoint -> pointLayersPanel
        LandmarkType.Line -> lineLayersPanel
        LandmarkType.Plane -> planeLayersPanel
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
