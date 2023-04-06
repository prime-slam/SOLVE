package solve.settings.visualization.fields.view

import javafx.application.Platform
import javafx.geometry.Insets
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
import solve.settings.visualization.popover.LineLayerSettingsPopOverNode
import solve.settings.visualization.popover.PointLayerSettingsPopOverNode
import solve.utils.*
import solve.utils.nodes.listcell.dragdrop.DragAndDropCellItemInfo
import solve.utils.nodes.listcell.dragdrop.DragAndDropListCell
import solve.utils.structures.DoublePoint
import tornadofx.*

class VisualizationSettingsLayerCell(
    private val sceneController: SceneController
) : DragAndDropListCell<LayerSettings>(LayerSettings::class) {
    override fun setAfterDragDropped(
        event: DragEvent,
        thisItemInfo: DragAndDropCellItemInfo<LayerSettings>,
        droppedItemInfo: DragAndDropCellItemInfo<LayerSettings>
    ) {
        val scene = sceneController.scene
        val thisItemGroupRenderIndex = getItemRenderIndex(thisItemInfo)
        val droppedItemGroupRenderIndex = getItemRenderIndex(droppedItemInfo)

        scene.changeLayerIndex(thisItemInfo.item, thisItemGroupRenderIndex)
        scene.changeLayerIndex(droppedItemInfo.item, droppedItemGroupRenderIndex)
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
        if (layerType != LandmarkType.Plane) {
            add(createLayerEditButton(layerType))
        }
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

    override fun isAbleToDropItem(thisItem: LayerSettings, droppedItem: LayerSettings): Boolean {
        val bothArePlanes =
            thisItem is LayerSettings.PlaneLayerSettings && droppedItem is LayerSettings.PlaneLayerSettings
        val bothAreNotPlanes =
            thisItem !is LayerSettings.PlaneLayerSettings && droppedItem !is LayerSettings.PlaneLayerSettings

        return bothArePlanes || bothAreNotPlanes
    }

    override fun setOnNotAbleToDropItem(
        event: DragEvent,
        thisItemInfo: DragAndDropCellItemInfo<LayerSettings>,
        droppedItemInfo: DragAndDropCellItemInfo<LayerSettings>?
    ) {
        if (droppedItemInfo != null && !isAbleToDropItem(thisItemInfo.item, droppedItemInfo.item)) {
            val hintPopOver = PopOver(label("Plane and non-plane layers cannot be swapped!") {
                padding = Insets(0.0, 10.0, 0.0, 10.0)
            })
            hintPopOver.arrowLocation = PopOver.ArrowLocation.RIGHT_TOP
            hintPopOver.show(this)
        }
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

        initializeLayerSettingsPopOver(this, this, layerType)
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
            }
        }
        alignment = Pos.CENTER_RIGHT
        paddingLeft = LayerVisibilityIconPaddingLeft
    }

    private fun calculatePopOverShowPosition(spawnNode: Node, layerType: LandmarkType): DoublePoint {
        val labelPosition = spawnNode.getScreenPosition()
        val popOverNodeSize = getPopOverNodeSize(layerType)
        val popOverNodeSizeOffsetVector = DoublePoint(popOverNodeSize?.x ?: 0.0, 0.0)

        return labelPosition - popOverNodeSizeOffsetVector + LayerSettingsSpawnPositionOffset
    }

    private fun initializeLayerSettingsPopOver(
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

    private fun showPopOver(popOver: PopOver, spawnNode: Node, showPosition: DoublePoint) {
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
            LandmarkType.Line ->
                LineLayerSettingsPopOverNode(
                    layerSettings as LayerSettings.LineLayerSettings,
                    sceneController
                ).getPopOverNode()
            LandmarkType.Plane -> null
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
        LandmarkType.Keypoint -> DoublePoint(
            PointLayerSettingsPopOverNode.LayerSettingsNodePrefWidth,
            PointLayerSettingsPopOverNode.LayerSettingsNodePrefHeight
        )
        LandmarkType.Line -> DoublePoint(
            LineLayerSettingsPopOverNode.LayerSettingsNodePrefWidth,
            LineLayerSettingsPopOverNode.LayerSettingsNodePrefHeight
        )
        LandmarkType.Plane -> null
    }

    private fun getLayerSettingsType(layerSettings: LayerSettings) = when (layerSettings) {
        is LayerSettings.PointLayerSettings -> LandmarkType.Keypoint
        is LayerSettings.LineLayerSettings -> LandmarkType.Line
        is LayerSettings.PlaneLayerSettings -> LandmarkType.Plane
    }

    // Returns an item index depending on its belonging to the planes group and a render layer.
    // Planes group has indices separate from the other types of the landmarks.
    // Render index has a reverse order to the layer cell index.
    private fun getItemRenderIndex(
        itemInfo: DragAndDropCellItemInfo<LayerSettings>
    ): Int {
        val groupItems = if (itemInfo.item is LayerSettings.PlaneLayerSettings) {
            listView.items.filtered { it is LayerSettings.PlaneLayerSettings }
        } else {
            listView.items.filtered { it !is LayerSettings.PlaneLayerSettings }
        }
        val groupIndex = groupItems.indexOf(itemInfo.item)

        return groupItems.lastIndex - groupIndex
    }

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

        private val LayerSettingsSpawnPositionOffset = DoublePoint(-135.0, 25.0)

        private val pointLayerIconImage = loadResourcesImage(IconsVisualizationSettingsPointLayerPath)
        private val lineLayerIconImage = loadResourcesImage(IconsVisualizationSettingsLineLayerPath)
        private val planeLayerIconImage = loadResourcesImage(IconsVisualizationSettingsPlaneLayerPath)
        private val editIconImage = loadResourcesImage(IconsVisualizationSettingsEditPath)
        private val layerVisibleIconImage = loadResourcesImage(IconsVisualizationSettingsLayerVisiblePath)
        private val layerInvisibleIconImage = loadResourcesImage(IconsVisualizationSettingsLayerInvisiblePath)
    }
}

class VisualizationSettingsLayerCellStyle: Stylesheet() {
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

    companion object {
        private val LayerEditButtonBackgroundColor = Color.TRANSPARENT
        private const val HoveredEditButtonScale = 1.25
    }
}
