package solve.settings.visualization.fields.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration
import org.controlsfx.control.PopOver
import solve.scene.controller.SceneController
import solve.scene.model.LandmarkType
import solve.scene.model.LayerSettings
import solve.settings.visualization.popover.PointLayerSettingsPopOverNode
import solve.utils.*
import solve.utils.nodes.DragAndDropListCell
import solve.utils.structures.Point
import tornadofx.*

class VisualizationSettingsLayerCell(
    private val fieldsListView: ListView<LayerSettings>,
    private val sceneController: SceneController
) : DragAndDropListCell<LayerSettings>() {
    companion object {
        private const val LayerFieldHeight = 20.0
        private const val LayerFieldNameMaxWidth = 80.0
        private const val LayerFieldNameFontSize = 12.0
        private const val LayerFieldEditIconSize = 16.0
        private const val LayerVisibilityIconSize = 20.0
        private const val LayerIconWidth = 16.0

        private const val LayerFieldHBoxPaddingRight = -2.5
        private const val LayerTypeIconPaddingRight = 5.0

        private val LayerSettingsSpawnPositionOffset = Point(-20.0, 30.0)

        private val pointLayerIconImage = loadImage("icons/visualization_settings_point_icon.png")
        private val lineLayerIconImage = loadImage("icons/visualization_settings_line_icon.png")
        private val planeLayerIconImage = loadImage("icons/visualization_settings_plane_icon.png")
        private val editIconImage = loadImage("icons/visualization_settings_edit_icon.png")
        private val layerVisibleIconImage = loadImage("icons/visualization_settings_layer_visible_icon.png")
        private val layerInvisibleIconImage = loadImage("icons/visualization_settings_layer_invisible.png")
    }

    override fun isListViewCellSource(gestureSource: Any) =
        gestureSource is VisualizationSettingsLayerCell && fieldsListView.items.contains(item)

    override fun createItemCellGraphic(item: LayerSettings): Node = hbox {
        prefHeight = LayerFieldHeight
        addStylesheet(VisualizationSettingsLayerCellStyle::class)

        val layerIcon = getLayerIcon(item.landmarksType)
        if (layerIcon != null) {
            // Needed to set the padding and to center the imageview.
            val imageViewIconRegion = vbox {
                add(createVGrowBox())
                add(createImageViewIcon(layerIcon, LayerIconWidth))
                add(createVGrowBox())
                paddingRight = LayerTypeIconPaddingRight
            }
            add(imageViewIconRegion)
        }
        label(item.name) {
            font = Font.font(LayerFieldNameFontSize)
            maxWidth = LayerFieldNameMaxWidth
        }
        add(createHGrowHBox())
        button {
            editIconImage ?: return@button
            graphic = createImageViewIcon(editIconImage, LayerFieldEditIconSize)
            isPickOnBounds = false

            initializePopOver(this, this)
            alignment = Pos.CENTER_RIGHT
        }
        button {
            layerVisibleIconImage ?: return@button
            layerInvisibleIconImage ?: return@button
            var isVisible = true
            val visibleImageViewIcon = createImageViewIcon(layerVisibleIconImage, LayerVisibilityIconSize)
            val invisibleImageViewIcon = createImageViewIcon(layerInvisibleIconImage, LayerVisibilityIconSize)

            graphic = visibleImageViewIcon
            action {
                isVisible = !isVisible
                graphic = if (isVisible) visibleImageViewIcon else invisibleImageViewIcon
                item.enabled = isVisible
            }
            alignment = Pos.CENTER_RIGHT
        }

        alignment = Pos.CENTER_LEFT
        paddingRight = LayerFieldHBoxPaddingRight
    }

    private fun initializePopOver(
        layerSettingsButton: Button,
        spawnNode: Node
    ) {
        val popOverTitle = "${item.name} (${item.landmarksType.name})"
        val popOverNode = createLayerSettingsPopOverNode(item)
        if (popOverNode != null) {
            val popOver = createLayerSettingsPopOver(
                popOverNode,
                popOverTitle
            )
            var isPopOverShowing = false

            // Installing a correct popover position.
            val labelPosition = spawnNode.getScreenPosition()
            val popOverNodeSize = getPopOverNodeSize(item.landmarksType)

            if (popOverNodeSize != null) {
                val popOverNodeSizeOffsetVector = Point(popOverNodeSize.x, 0.0)
                val showPosition = labelPosition - popOverNodeSizeOffsetVector + LayerSettingsSpawnPositionOffset

                popOver.setOnHidden {
                    isPopOverShowing = false
                }
                popOver.setOnShowing {
                    isPopOverShowing = true
                }
                layerSettingsButton.action {
                    if (!isPopOverShowing) {
                        showPopOver(popOver, spawnNode, showPosition)
                    } else {
                        popOver.hide()
                    }
                }
            }
        }
    }

    private fun showPopOver(popOver: PopOver, spawnNode: Node, showPosition: Point) {
        popOver.detach()
        popOver.show(spawnNode, showPosition.x, showPosition.y)
    }

    private fun createLayerSettingsPopOverNode(layerSettings: LayerSettings): Node? =
        when (layerSettings.landmarksType) {
            LandmarkType.Keypoint ->
                PointLayerSettingsPopOverNode(
                    layerSettings as LayerSettings.PointLayerSettings,
                    sceneController
                ).getPopOverNode()
            LandmarkType.Line -> null // TODO: add a line layers panel realization.
            LandmarkType.Plane -> null // TODO: add a plane layers panel realization.
        }

    private fun createLayerSettingsPopOver(contentNode: Node, titleLabel: String): PopOver
    {
        val popOver = PopOver(contentNode)
        popOver.detach()
        popOver.title = titleLabel

        Platform.runLater {
            // Needed for the safe window closing.
            listView.scene.window?.setOnCloseRequest {
                popOver.hide(Duration.ZERO)
            }
        }

        return popOver
    }

    private fun getLayerIcon(layerType: LandmarkType) = when(layerType) {
        LandmarkType.Keypoint -> pointLayerIconImage
        LandmarkType.Line -> lineLayerIconImage
        LandmarkType.Plane -> planeLayerIconImage
    }

    private fun getPopOverNodeSize(layerType: LandmarkType) = when(layerType) {
        LandmarkType.Keypoint -> Point(
            PointLayerSettingsPopOverNode.LayerSettingsNodePrefWidth,
            PointLayerSettingsPopOverNode.LayerSettingsNodePrefHeight
        )
        LandmarkType.Line -> null // TODO: add a line layers panel realization.
        LandmarkType.Plane -> null // TODO: add a plane layers panel realization.
    }
}

class VisualizationSettingsLayerCellStyle: Stylesheet() {
    companion object {
        private val LayerEditButtonBackgroundColor = Color.TRANSPARENT
        private const val HoveredEditButtonScale = 1.25
    }
    init {
        button {
            backgroundColor += LayerEditButtonBackgroundColor

            and(hover) {
                imageView {
                    scale(HoveredEditButtonScale)
                }
            }
        }
    }
}
