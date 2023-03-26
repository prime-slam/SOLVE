package solve.settings.visualization.fields.view

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBoxBase
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.DragEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.util.Duration
import org.controlsfx.control.PopOver
import solve.constants.*
import solve.scene.controller.SceneController
import solve.scene.model.LandmarkType
import solve.scene.model.LayerSettings
import solve.settings.visualization.popover.PointLayerSettingsPopOverNode
import solve.utils.*
import solve.utils.nodes.DragAndDropCellItemInfo
import solve.utils.nodes.DragAndDropListCell
import solve.utils.structures.Point
import tornadofx.*

class VisualizationSettingsLayerCell(
    private val sceneController: SceneController
) : DragAndDropListCell<LayerSettings>() {
    companion object {
        private const val LayerFieldHeight = 30.0
        private const val LayerFieldNameMaxWidth = 80.0
        private const val LayerFieldNameFontSize = 13.0
        private const val LayerFieldEditIconSize = 18.0
        private const val LayerVisibilityIconSize = 22.0
        private const val LayerIconWidth = 25.0

        private const val LayerFieldHBoxPaddingRight = -2.5
        private const val LayerTypeIconPaddingRight = 5.0
        private const val LayerVisibilityIconPaddingLeft = -5.0

        private val LayerSettingsSpawnPositionOffset = Point(-135.0, 25.0)

        private val pointLayerIconImage = loadResourcesImage(IconsVisualizationSettingsPointLayerPath)
        private val lineLayerIconImage = loadResourcesImage(IconsVisualizationSettingsLineLayerPath)
        private val planeLayerIconImage = loadResourcesImage(IconsVisualizationSettingsPlaneLayerPath)
        private val editIconImage = loadResourcesImage(IconsVisualizationSettingsEditPath)
        private val layerVisibleIconImage = loadResourcesImage(IconsVisualizationSettingsLayerVisiblePath)
        private val layerInvisibleIconImage = loadResourcesImage(IconsVisualizationSettingsLayerInvisiblePath)
    }

    override fun setOnDragDropped(
        event: DragEvent,
        thisItemInfo: DragAndDropCellItemInfo<LayerSettings>,
        droppedItemInfo: DragAndDropCellItemInfo<LayerSettings>
    ) {
        val scene = sceneController.sceneProperty.value
        scene.changeLayerIndex(thisItemInfo.item, droppedItemInfo.index)
        scene.changeLayerIndex(droppedItemInfo.item, thisItemInfo.index)
    }

    override fun createItemCellGraphic(item: LayerSettings): Node = hbox {
        val layerType = getLayerSettingsType(item)

        prefHeight = LayerFieldHeight
        addStylesheet(VisualizationSettingsLayerCellStyle::class)

        val layerIconNode = createLayerIconNode(layerType)
        if (layerIconNode != null) {
            add(layerIconNode)
        }
        add(createLayerNameLabel())
        add(createHGrowHBox())
        add(createLayerEditButton(layerType))
        add(createLayerVisibilityButton())

        alignment = Pos.CENTER_LEFT
        paddingRight = LayerFieldHBoxPaddingRight
    }

    override fun createItemDragView(item: LayerSettings): Image {
        val snapshotNode = createItemCellGraphic(item)

        children.remove(snapshotNode)
        Scene(snapshotNode as Parent)
        return snapshotNode.createSnapshot()
    }

    private fun createLayerIconNode(layerType: LandmarkType): Node? {
        val layerIcon = getLayerIcon(layerType)
        layerIcon ?: return null
        // Needed to set the padding and to center the imageview.
        return vbox {
            add(createVGrowBox())
            add(createImageViewIcon(layerIcon, LayerIconWidth))
            add(createVGrowBox())
            paddingRight = LayerTypeIconPaddingRight
        }
    }

    private fun createLayerNameLabel(): Label = label(item.layerName) {
        font = Font.font(LayerFieldNameFontSize)
        maxWidth = LayerFieldNameMaxWidth
    }

    private fun createLayerEditButton(layerType: LandmarkType): Node = button {
        editIconImage ?: return@button
        val editImageViewIcon = createImageViewIcon(editIconImage, LayerFieldEditIconSize)
        graphic = editImageViewIcon
        isPickOnBounds = false

        initializePopOver(this, this, layerType)
        alignment = Pos.CENTER_RIGHT
    }

    private fun createLayerVisibilityButton(): Node = hbox {
        layerVisibleIconImage ?: return@hbox
        layerInvisibleIconImage ?: return@hbox
        val layerVisibleImageViewIcon = createImageViewIcon(layerVisibleIconImage, LayerVisibilityIconSize)
        val layerInvisibleImageViewIcon =
            createImageViewIcon(layerInvisibleIconImage, LayerVisibilityIconSize)

        fun getCurrentVisibilityImageViewIcon() =
            if (item.enabled) layerVisibleImageViewIcon else layerInvisibleImageViewIcon

        button {
            graphic = getCurrentVisibilityImageViewIcon()
            action {
                item.enabled = !item.enabled
                graphic = getCurrentVisibilityImageViewIcon()
                item.enabled = item.enabled
            }
        }
        alignment = Pos.CENTER_RIGHT
        paddingLeft = LayerVisibilityIconPaddingLeft
    }

    private fun calculatePopOverShowPosition(spawnNode: Node, layerType: LandmarkType): Point {
        val labelPosition = spawnNode.getScreenPosition()
        val popOverNodeSize = getPopOverNodeSize(layerType)
        val popOverNodeSizeOffsetVector = Point(popOverNodeSize?.x ?: 0.0, 0.0)

        return labelPosition - popOverNodeSizeOffsetVector + LayerSettingsSpawnPositionOffset
    }

    private fun initializePopOver(
        layerSettingsButton: Button,
        spawnNode: Node,
        layerType: LandmarkType
    ) {
        val popOverTitle = "${item.layerName} (${layerType.name})"
        val popOverNode = createLayerSettingsPopOverNode(item)
        if (popOverNode != null) {
            val popOver = createLayerSettingsPopOver(
                popOverNode,
                popOverTitle
            )
            var isPopOverShowing = false

            popOver.setOnHidden {
                isPopOverShowing = false
            }
            popOver.setOnShowing {
                isPopOverShowing = true
            }
            // Needed in order not to lose the focus of the main window when opening a modal window.
            popOver.addEventFilter(ComboBoxBase.ON_HIDDEN) { event ->
                event.consume()
                scene.window.requestFocus()
            }
            layerSettingsButton.action {
                if (!isPopOverShowing) {
                    val showPosition = calculatePopOverShowPosition(spawnNode, layerType)
                    showPopOver(popOver, spawnNode, showPosition)
                } else {
                    popOver.hide()
                }
            }
        }
    }

    private fun showPopOver(popOver: PopOver, spawnNode: Node, showPosition: Point) {
        popOver.detach()
        popOver.show(spawnNode, showPosition.x, showPosition.y)
    }

    private fun createLayerSettingsPopOverNode(layerSettings: LayerSettings): Node? =
        when (getLayerSettingsType(layerSettings)) {
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
            listView.scene?.window?.setOnCloseRequest {
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

    private fun getLayerSettingsType(layerSettings: LayerSettings) = when (layerSettings) {
        is LayerSettings.PointLayerSettings -> LandmarkType.Keypoint
        is LayerSettings.LineLayerSettings -> LandmarkType.Line
        is LayerSettings.PlaneLayerSettings -> LandmarkType.Plane
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
