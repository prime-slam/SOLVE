package solve.scene.view

import io.github.palexdev.materialfx.controls.MFXContextMenu
import javafx.beans.*
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
import javafx.scene.shape.Shape
import javafx.stage.Window
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import solve.scene.model.*
import solve.scene.view.association.AssociationLine
import solve.scene.view.association.AssociationsManager
import solve.utils.*
import tornadofx.*

class FrameView(
    private val width: Double,
    private val height: Double,
    private val scale: DoubleProperty,
    private val coroutineScope: CoroutineScope,
    private val associationsManager: AssociationsManager,
    private val orderManager: OrderManager<LayerSettings>,
    frame: VisualizationFrame?
) : Group() {
    private var drawnLandmarks: Map<Layer, List<LandmarkView>>? = null
    private var drawnImage: Image? = null
    private var currentFrame: VisualizationFrame? = null
    private val canvas = BufferedImageView(width, height, scale.value)
    private var currentJob: Job? = null

    // Should be stored to avoid weak listener from be collected
    private val scaleChangedListener = InvalidationListener { scaleImageAndLandmarks(scale.value) }
    private val associationsUpdatedListener =
        MapChangeListener<AssociationsManager.AssociationKey, Map<VisualizationFrame, List<AssociationLine>>> { change ->
            hasAssociations.value = getAssociatedLayersNames(currentFrame ?: return@MapChangeListener).isNotEmpty()
        }

    private val hasKeypoints = SimpleBooleanProperty(frame?.hasPoints() ?: false)
    private val hasAssociations = SimpleBooleanProperty(false)

    init {
        scale.addListener(WeakInvalidationListener(scaleChangedListener))
        associationsManager.drawnAssociations.addListener(associationsUpdatedListener)

        canvas.viewOrder = IMAGE_VIEW_ORDER
        add(canvas)
        setFrame(frame)
        scaleImageAndLandmarks(scale.value)

        addChooseSecondAssociationFrameAction()

        mfxContextMenu {
            addCopyTimestampAction()
            lineSeparator()
            addAssociationActions()
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
            associationsManager.associate(associationParameters, layer.settings.commonColor)
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
        return associationsManager.drawnAssociations.filter { it.key.frame == frame }.map { it.key.layerName }
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

    fun setFrame(frame: VisualizationFrame?) {
        currentJob?.cancel()
        disposeLandmarkViews()
        removeLandmarksNodes()
        canvas.clear()

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
                        LandmarkView.create(landmark, scale.value, canvas)
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
        hasKeypoints.value = frame.hasPoints()
        hasAssociations.value = getAssociatedLayersNames(frame).isNotEmpty()
    }

    fun dispose() {
        disposeLandmarkViews()
        associationsManager.drawnAssociations.removeListener(associationsUpdatedListener)
    }

    private fun draw() {
        val image = drawnImage ?: return
        canvas.clear()
        canvas.drawImage(image)

        drawnLandmarks = drawnLandmarks?.toSortedMap(compareBy { layer -> orderManager.indexOf(layer.settings) })

        doForAllLandmarks { view, layerIndex ->
            if (view.node != null) {
                view.node?.viewOrder = LANDMARKS_VIEW_ORDER - layerIndex
            }
            view.drawOnCanvas()
        }
    }

    private val orderChangedCallback = {
        draw()
    }

    init {
        orderManager.addOrderChangedListener(orderChangedCallback)
    }

    private fun disposeLandmarkViews() = doForAllLandmarks { view, _ -> view.dispose() }

    private fun scaleImageAndLandmarks(newScale: Double) {
        canvas.scale(newScale)
        doForAllLandmarks { view, _ -> view.scale = newScale }
    }

    private fun validateImage(image: Image) {
        if (image.height != height || image.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }
    }

    private fun removeLandmarksNodes() = children.removeIf { x -> x is Shape }

    private fun addLandmarksNodes() = doForAllLandmarks { view, _ ->
        if (view.node != null) {
            children.add(view.node)
        }
    }

    private fun drawLoadingIndicator() = canvas.fill(Color.GREY)

    private fun doForAllLandmarks(delegate: (LandmarkView, Int) -> Unit) =
        drawnLandmarks?.values?.forEachIndexed { layerIndex, landmarkViews ->
            landmarkViews.forEach { view -> delegate(view, layerIndex) }
        }

    private fun VisualizationFrame.hasPoints() = this.layers.filterIsInstance<Layer.PointLayer>().isNotEmpty()
}
