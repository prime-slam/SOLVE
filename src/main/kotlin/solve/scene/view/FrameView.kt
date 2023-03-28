package solve.scene.view

import RectangleFrameElement
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import solve.scene.model.Landmark
import solve.scene.model.Layer
import solve.scene.model.LayerSettings
import solve.scene.model.OrderManager
import solve.scene.model.VisualizationFrame
import solve.scene.view.association.AssociationsManager
import solve.scene.view.drawing.BufferedImageView
import solve.scene.view.drawing.FrameDrawer
import solve.scene.view.drawing.ImageFrameElement
import solve.utils.structures.Size as DoubleSize
import tornadofx.action
import tornadofx.add
import tornadofx.contextmenu
import tornadofx.item

class FrameView(
    private val size: DoubleSize,
    private val scale: DoubleProperty,
    private val coroutineScope: CoroutineScope,
    private val associationsManager: AssociationsManager,
    private val orderManager: OrderManager<LayerSettings>,
    canvasLayersCount: Int,
    frame: VisualizationFrame?
) : Group() {
    private var drawnLandmarks: Map<Layer, List<LandmarkView>>? = null
    private var drawnImage: Image? = null
    private var currentFrame: VisualizationFrame? = null
    private val canvas = BufferedImageView(size.width, size.height, scale.value)
    private val frameDrawer = FrameDrawer(canvas, canvasLayersCount + 1)
    private var currentJob: Job? = null

    // Should be stored to avoid weak listener from be collected
    private val scaleChangedListener = InvalidationListener { scaleImageAndLandmarks(scale.value) }

    init {
        scale.addListener(WeakInvalidationListener(scaleChangedListener))

        canvas.viewOrder = IMAGE_VIEW_ORDER
        add(canvas)
        setFrame(frame)
        scaleImageAndLandmarks(scale.value)

        setOnMouseClicked {
            if (it.button != MouseButton.PRIMARY) {
                return@setOnMouseClicked
            }
            val clickedFrame = currentFrame ?: return@setOnMouseClicked
            if (!clickedFrame.hasPoints()) {
                return@setOnMouseClicked
            }
            val associationParameters =
                AssociationsManager.AssociationParameters(clickedFrame, getKeypoints(clickedFrame))
            associationsManager.chooseFrame(associationParameters)
        }

        contextmenu {
            item("Associate keypoints").action {
                val clickedFrame = currentFrame ?: return@action
                if (!clickedFrame.hasPoints()) {
                    return@action
                }
                val associationParameters =
                    AssociationsManager.AssociationParameters(clickedFrame, getKeypoints(clickedFrame))
                associationsManager.initAssociation(associationParameters)
            }
            item("Clear associations").action {
                val clickedFrame = currentFrame ?: return@action
                associationsManager.clearAssociation(clickedFrame)
            }
        }
    }

    private fun getKeypoints(frame: VisualizationFrame): List<Landmark.Keypoint> {
        val layer = frame.layers.filterIsInstance<Layer.PointLayer>().first() // TODO: more than one layer in a frame
        return layer.getLandmarks()
    }

    fun setFrame(frame: VisualizationFrame?) {
        currentJob?.cancel()
        disposeLandmarkViews()
        removeLandmarksNodes()
        frameDrawer.clear()
        frameDrawer.fullRedraw()

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
                            landmark, orderManager.indexOf(it.key.settings), scale.value, frameDrawer, canvas
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
        disposeLandmarkViews()
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
        if (image.height != size.height || image.width != size.width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }
    }

    private fun removeLandmarksNodes() {
        doForAllLandmarks { view, _ -> if (view.node != null) { children.remove(view.node) } }
    }

    private fun addLandmarksNodes() = doForAllLandmarks { view, _ ->
        if (view.node != null) {
            children.add(view.node)
        }
    }

    private fun drawLoadingIndicator() {
        frameDrawer.addOrUpdateElement(
            RectangleFrameElement(
                FrameDrawer.IMAGE_VIEW_ORDER, Color.GREY, frameDrawer.width, frameDrawer.height
            )
        )
        frameDrawer.fullRedraw()
    }

    private fun doForAllLandmarks(delegate: (LandmarkView, Int) -> Unit) =
        drawnLandmarks?.values?.forEachIndexed { layerIndex, landmarkViews ->
            landmarkViews.forEach { view -> delegate(view, layerIndex) }
        }

    private fun VisualizationFrame.hasPoints() = this.layers.filterIsInstance<Layer.PointLayer>().isNotEmpty()
}
