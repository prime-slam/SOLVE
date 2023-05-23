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
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.text.Font
import javafx.util.Duration
import org.controlsfx.control.PopOver
import solve.constants.IconsSettingsVisualizationEditPath
import solve.constants.IconsSettingsVisualizationLayerInvisiblePath
import solve.constants.IconsSettingsVisualizationLayerVisiblePath
import solve.constants.IconsSettingsVisualizationLineLayerPath
import solve.constants.IconsSettingsVisualizationPlaneLayerPath
import solve.constants.IconsSettingsVisualizationPointLayerPath
import solve.scene.controller.SceneController
import solve.scene.model.LandmarkType
import solve.scene.model.LayerSettings
import solve.settings.visualization.popover.LineLayerSettingsPopOverNode
import solve.settings.visualization.popover.PointLayerSettingsPopOverNode
import solve.utils.TransparentScalingButtonStyle
import solve.utils.createHGrowHBox
import solve.utils.createSnapshot
import solve.utils.createVGrowBox
import solve.utils.getScreenPosition
import solve.utils.imageViewIcon
import solve.utils.loadResourcesImage
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

    private fun createItemCellGraphicSnapshot(item: LayerSettings): Node = hbox(5) {
        val layerType = getLayerSettingsType(item)

        prefHeight = LayerFieldHeight
        addStylesheet(TransparentScalingButtonStyle::class)

        val layerIconNode = createLayerIconNode(layerType)
        if (layerIconNode != null) {
            add(layerIconNode)
        }
        add(createLayerNameLabel())
        add(createHGrowHBox())
        hbox(8) {
            val editIconImageViewIcon = createEditIconImageViewIcon()
            val visibilityImageViewIcon = createVisibilityImageViewIcon(item.enabled)
            if (editIconImageViewIcon != null)
                add(editIconImageViewIcon)
            if (visibilityImageViewIcon != null)
                add(visibilityImageViewIcon)

            paddingTop = 3.0
        }

        alignment = Pos.CENTER_LEFT
        paddingRight = LayerFieldHBoxPaddingRight
    }

    override fun createItemCellGraphic(item: LayerSettings): Node = hbox {
        val layerType = getLayerSettingsType(item)

        prefHeight = LayerFieldHeight
        addStylesheet(TransparentScalingButtonStyle::class)

        val layerIconNode = createLayerIconNode(layerType)
        if (layerIconNode != null) {
            add(layerIconNode)
        }
        add(createLayerNameLabel())
        add(createHGrowHBox())
        if (layerType != LandmarkType.Plane) {
            add(createLayerEditButton(layerType))
        }
        add(createLayerVisibilityButtonNode())

        alignment = Pos.CENTER_LEFT
        paddingRight = LayerFieldHBoxPaddingRight
    }

    override fun createItemDragView(item: LayerSettings): Image {
        val snapshotNode = createItemCellGraphicSnapshot(item)

        children.remove(snapshotNode)
        Scene(snapshotNode as Parent)
        return snapshotNode.createSnapshot()
    }

    override fun isAbleToDropItem(thisItem: LayerSettings, droppedItem: LayerSettings): Boolean {
        val bothUseCanvas = thisItem.usesCanvas && droppedItem.usesCanvas
        val bothNotUseCanvas = !thisItem.usesCanvas && !droppedItem.usesCanvas

        return bothUseCanvas || bothNotUseCanvas
    }

    override fun setOnNotAbleToDropItem(
        event: DragEvent,
        thisItemInfo: DragAndDropCellItemInfo<LayerSettings>,
        droppedItemInfo: DragAndDropCellItemInfo<LayerSettings>?
    ) {
        if (droppedItemInfo != null && !isAbleToDropItem(thisItemInfo.item, droppedItemInfo.item)) {
            val hintPopOver = PopOver(
                label("Plane and non-plane layers cannot be swapped!") {
                    padding = Insets(0.0, 10.0, 0.0, 10.0)
                }
            )
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
            imageViewIcon(layerIcon, LayerIconWidth)
            add(createVGrowBox())
            paddingRight = LayerTypeIconPaddingRight
        }
    }

    private fun createLayerNameLabel(): Label = label(item.layerName) {
        font = Font.font(LayerFieldNameFontSize)
        maxWidth = LayerFieldNameMaxWidth
    }

    private fun createEditIconImageViewIcon(): ImageView? {
        return imageViewIcon(editIconImage ?: return null, LayerFieldEditIconSize)
    }

    private fun createLayerEditButton(layerType: LandmarkType): Node = button {
        editIconImage ?: return@button
        graphic = createEditIconImageViewIcon()
        isPickOnBounds = false

        initializeLayerSettingsPopOver(this, this, layerType)
        alignment = Pos.CENTER_RIGHT
    }

    private fun createVisibilityImageViewIcon(isVisible: Boolean): ImageView? {
        if (isVisible) {
            return imageViewIcon(layerVisibleIconImage ?: return null, LayerVisibilityIconSize)
        }

        return imageViewIcon(layerInvisibleIconImage ?: return null, LayerVisibilityIconSize)
    }

    private fun createLayerVisibilityButtonNode(): Node {
        val layerVisibilityButtonNode = hbox {
            button {
                graphic = createVisibilityImageViewIcon(item.enabled)
                action {
                    item.enabled = !item.enabled
                    graphic = createVisibilityImageViewIcon(item.enabled)
                }
            }
            alignment = Pos.CENTER_RIGHT
            paddingLeft = LayerVisibilityIconPaddingLeft
        }

        return layerVisibilityButtonNode
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

    private fun createLayerSettingsPopOver(contentNode: Node, titleLabel: String): PopOver {
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

    private fun getLayerIcon(layerType: LandmarkType) = when (layerType) {
        LandmarkType.Keypoint -> pointLayerIconImage
        LandmarkType.Line -> lineLayerIconImage
        LandmarkType.Plane -> planeLayerIconImage
    }

    private fun getPopOverNodeSize(layerType: LandmarkType) = when (layerType) {
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
        val groupItems = if (itemInfo.item.usesCanvas) {
            listView.items.filtered { it.usesCanvas }
        } else {
            listView.items.filtered { !it.usesCanvas }
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

        private val pointLayerIconImage = loadResourcesImage(IconsSettingsVisualizationPointLayerPath)
        private val lineLayerIconImage = loadResourcesImage(IconsSettingsVisualizationLineLayerPath)
        private val planeLayerIconImage = loadResourcesImage(IconsSettingsVisualizationPlaneLayerPath)
        private val editIconImage = loadResourcesImage(IconsSettingsVisualizationEditPath)
        private val layerVisibleIconImage = loadResourcesImage(IconsSettingsVisualizationLayerVisiblePath)
        private val layerInvisibleIconImage = loadResourcesImage(IconsSettingsVisualizationLayerInvisiblePath)
    }
}