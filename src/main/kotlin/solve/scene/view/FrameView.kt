package solve.scene.view

import io.github.palexdev.materialfx.controls.MFXContextMenu
import javafx.beans.InvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.MapChangeListener
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.stage.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solve.scene.model.Layer
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationLine
import solve.scene.view.association.AssociationsManager
import solve.scene.view.drawing.*
import solve.scene.view.landmarks.LandmarkView
import solve.utils.*
import tornadofx.*
import tornadofx.add
import solve.utils.structures.Size as DoubleSize

class FrameView(
    val size: DoubleSize,
    private val scale: DoubleProperty,
    private val frameViewStorage: Storage<FrameView>,
    canvasLayersCount: Int,
    parameters: FrameViewParameters,
    frame: VisualizationFrame?
) : Group(), CacheElement<FrameViewData>, Updatable<VisualizationFrame?> {
    private var coroutineScope = parameters.coroutineScope
    private var associationsManager = parameters.associationsManager
    private var orderManager = parameters.orderManager

    private var drawnLandmarks: Map<Layer, List<LandmarkView>>? = null
    private var drawnImage: Image? = null
    private var currentFrame: VisualizationFrame? = null
    private val canvas = BufferedImageView(size.width, size.height, scale.value)
    private val frameDrawer = FrameDrawer(canvas, canvasLayersCount + 1)
    private val frameEventManager = FrameEventManager(canvas, scale)
    private var currentJob: Job? = null

    private val scaleChangedListener = InvalidationListener { scaleImageAndLandmarks(scale.value) }

    private val orderChangedCallback = {
        draw()
    }

    private val associationsUpdatedListener =
        MapChangeListener<AssociationsManager.AssociationKey, Map<VisualizationFrame, List<AssociationLine>>> {
            hasAssociations.value = getAssociatedLayersNames(currentFrame ?: return@MapChangeListener).isNotEmpty()
        }

    private val hasKeypoints = SimpleBooleanProperty(frame?.hasPoints() ?: false)
    private val hasAssociations = SimpleBooleanProperty(false)

    init {
        canvas.viewOrder = IMAGE_VIEW_ORDER
        add(canvas)
        init(FrameViewData(frame, parameters))

        addAssociationListeners()
        addChooseSecondAssociationFrameAction()

        mfxContextMenu {
            addCopyTimestampAction()
            lineSeparator()
            addAssociationActions()
        }
    }

    private fun Node.addAssociationListeners() {
        setOnContextMenuRequested {
            hasKeypoints.value = currentFrame?.hasPoints()
            hasAssociations.value =
                getAssociatedLayersNames(currentFrame ?: return@setOnContextMenuRequested).isNotEmpty()
        }
    }

    private fun Node.addChooseSecondAssociationFrameAction() {
        setOnMouseClicked { mouse ->
            if (mouse.button != MouseButton.PRIMARY) {
                return@setOnMouseClicked
            }
            val clickedFrame = currentFrame ?: return@setOnMouseClicked
            val chosenLayerName = associationsManager.chosenLayerName ?: return@setOnMouseClicked
            val layer = clickedFrame.layers.filterIsInstance<Layer.PointLayer>().single { it.name == chosenLayerName }
            val associationKey = AssociationsManager.AssociationKey(clickedFrame, layer.name)
            val associationParameters = AssociationsManager.AssociationParameters(associationKey, layer.getLandmarks())
            associationsManager.associate(
                associationParameters,
                layer.settings.commonColorProperty,
                layer.settings.enabledProperty
            )
        }
    }

    private fun MFXContextMenu.addCopyTimestampAction() {
        item("Copy timestamp").action {
            val timestamp = currentFrame?.timestamp ?: return@action
            val clipboardContent = ClipboardContent().also { it.putString(timestamp.toString()) }
            Clipboard.getSystemClipboard().setContent(clipboardContent)
        }
    }

    private fun MFXContextMenu.addAssociationActions() {
        item("Associate keypoints").also { it.enableWhen(hasKeypoints) }.action {
            val clickedFrame = currentFrame ?: return@action
            val layer = choosePointLayer(clickedFrame, ownerWindow) ?: return@action
            val associationKey = AssociationsManager.AssociationKey(clickedFrame, layer.name)
            val associationParameters = AssociationsManager.AssociationParameters(associationKey, layer.getLandmarks())
            associationsManager.initAssociation(associationParameters)
        }

        item("Clear associations").also { it.enableWhen(hasAssociations) }.action {
            val clickedFrame = currentFrame ?: return@action
            val layer = chooseAssociatedPointLayer(clickedFrame, ownerWindow) ?: return@action
            val associationKey = AssociationsManager.AssociationKey(clickedFrame, layer.name)
            associationsManager.clearAssociation(associationKey)
        }
    }

    private fun chooseAssociatedPointLayer(frame: VisualizationFrame, owner: Window): Layer.PointLayer? {
        val associatedLayersNames = getAssociatedLayersNames(frame)
        val enabledAssociatedPointLayers = frame.layers
            .filterIsInstance<Layer.PointLayer>()
            .filter { layer -> associatedLayersNames.any { it == layer.name } }
            .filter { it.settings.enabled }
        return chooseLayer(enabledAssociatedPointLayers, owner)
    }

    private fun getAssociatedLayersNames(frame: VisualizationFrame): List<String> {
        val layerNames = associationsManager.drawnAssociations.filter { it.key.frame == frame }.map { it.key.layerName }
        return layerNames.filter { layerName -> frame.layers.any { it.name == layerName && it.settings.enabled } }
    }

    private fun choosePointLayer(frame: VisualizationFrame, owner: Window): Layer.PointLayer? {
        val enabledPointLayers = frame.layers
            .filterIsInstance<Layer.PointLayer>()
            .filter { it.settings.enabled }
        return chooseLayer(enabledPointLayers, owner)
    }

    private fun <T> chooseLayer(layers: List<T>, owner: Window): T? {
        if (layers.isEmpty()) {
            return null
        }

        return if (layers.count() == 1) {
            layers.single()
        } else {
            val chooserDialog = ChooserDialog<T>("Choose layer", 200.0, 150.0, owner)
            chooserDialog.choose(layers)
        }
    }

    override fun init(params: FrameViewData) {
        scale.addListener(scaleChangedListener)
        setFrame(params.frame)
        updateParameters(params.frameViewParameters)
        scaleImageAndLandmarks(scale.value)
    }

    private fun updateParameters(parameters: FrameViewParameters) {
        coroutineScope = parameters.coroutineScope
        associationsManager = parameters.associationsManager
        associationsManager.drawnAssociations.addListener(associationsUpdatedListener)
        orderManager = parameters.orderManager
        orderManager.addOrderChangedListener(orderChangedCallback)
    }

    override fun update(data: VisualizationFrame?) {
        setFrame(data)
    }

    fun setFrame(frame: VisualizationFrame?) {
        if (DelayedFramesUpdatesManager.shouldDelay) {
            DelayedFramesUpdatesManager.delayUpdate(this, frame)
            return
        }
        if (frame == currentFrame) {
            return
        }
        currentJob?.cancel()
        disposeLandmarkViews()
        removeLandmarksNodes()
        frameDrawer.clear()
        frameDrawer.fullRedraw()

        currentFrame = frame

        if (frame == null) {
            return
        }

        currentJob = Job()
        drawLoadingIndicator()

        coroutineScope.launch(currentJob!!) {
            if (!isActive) return@launch
            val landmarkData = frame.layers.associateWith { it.getLandmarks() }

            if (!isActive) return@launch
            val image = frame.getImage()

            withContext(Dispatchers.JavaFx) {
                if (!this@launch.isActive) return@withContext
                val landmarkViews = landmarkData.mapValues {
                    it.value.map { landmark ->
                        LandmarkView.create(
                            landmark,
                            orderManager.indexOf(it.key.settings),
                            scale.value,
                            frameDrawer,
                            frameEventManager
                        )
                    }
                }
                validateImage(image)
                drawnImage = image
                drawnLandmarks = landmarkViews
                draw()
                addLandmarksNodes()
            }
        }

        currentFrame = frame
    }

    fun dispose() {
        scale.removeListener(scaleChangedListener)
        associationsManager.drawnAssociations.removeListener(associationsUpdatedListener)
        orderManager.removeOrderChangedListener(orderChangedCallback)
        disposeLandmarkViews()
        frameViewStorage.store(this)
    }

    private fun draw() {
        val image = drawnImage ?: return
        frameDrawer.clear()
        frameDrawer.addOrUpdateElement(ImageFrameElement(FrameDrawer.IMAGE_VIEW_ORDER, image))

        drawnLandmarks = drawnLandmarks?.toSortedMap(compareBy { layer -> orderManager.indexOf(layer.settings) })

        doForAllLandmarks { view, layerIndex ->
            view.viewOrder = layerIndex
            view.addToFrameDrawer()
        }

        frameDrawer.fullRedraw()
    }

    private fun disposeLandmarkViews() = doForAllLandmarks { view, _ -> view.dispose() }

    private fun scaleImageAndLandmarks(newScale: Double) {
        canvas.scale(newScale)
        doForAllLandmarks { view, _ -> view.scale = newScale }
    }

    private fun validateImage(image: Image) {
        if (image.height != size.height || image.width != size.width) {
            println("Image size doesn't equal to the frame size") // TODO: warn user
        }
    }

    private fun removeLandmarksNodes() {
        doForAllLandmarks { view, _ ->
            if (view.node != null) {
                children.remove(view.node)
            }
        }
    }

    private fun addLandmarksNodes() = doForAllLandmarks { view, _ ->
        if (view.node != null) {
            children.add(view.node)
        }
    }

    private fun drawLoadingIndicator() {
        frameDrawer.addOrUpdateElement(
            RectangleFrameElement(
                FrameDrawer.IMAGE_VIEW_ORDER,
                Color.GREY,
                frameDrawer.width,
                frameDrawer.height
            )
        )
        frameDrawer.fullRedraw()
    }

    private fun doForAllLandmarks(delegate: (LandmarkView, Int) -> Unit) =
        drawnLandmarks?.values?.forEachIndexed { layerIndex, landmarkViews ->
            landmarkViews.forEach { view -> delegate(view, layerIndex) }
        }

    private fun VisualizationFrame.hasPoints() =
        this.layers.filterIsInstance<Layer.PointLayer>().any { it.settings.enabled }
}
