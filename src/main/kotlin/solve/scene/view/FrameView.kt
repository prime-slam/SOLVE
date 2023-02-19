package solve.scene.view

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import solve.scene.model.Landmark
import solve.scene.model.*
import solve.scene.view.association.AssociationsManager
import tornadofx.*

class FrameView(
    private val width: Double,
    private val height: Double,
    private val scale: DoubleProperty,
    private val coroutineScope: CoroutineScope,
    private val associationsManager: AssociationsManager,
    frame: VisualizationFrame?
) : Group() {
    private var landmarksViews: Map<Layer, List<LandmarkView>>? = null
    private var drawnImage: Image? = null
    private var currentFrame: VisualizationFrame? = null
    private val canvas = BufferedImageView(width, height, scale.value)
    private var currentJob: Job? = null

    // Should be stored to avoid weak listener from be collected
    private val scaleChangedListener = InvalidationListener { _ -> scaleImageAndLandmarks(scale.value) }

    init {
        scale.addListener(WeakInvalidationListener(scaleChangedListener))

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
        clearLandmarks()
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
                draw(landmarkData, image)
            }
        }

        currentFrame = frame
    }

    fun dispose() {
        disposeLandmarkViews()
    }

    private fun draw(landmarks: Map<Layer, List<Landmark>>, image: Image) {
        drawnImage = image
        if (image.height != height || image.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }

        canvas.clear()
        canvas.drawImage(image)

        landmarksViews = landmarks.mapValues {
            it.value.map { landmark ->
                LandmarkView.create(landmark, scale.value)
            }
        }
        doForAllLandmarks { view ->
            if (view.node != null) {
                children.add(view.node)
            }
            view.drawOnCanvas(canvas)
        }
    }

    private fun disposeLandmarkViews() = doForAllLandmarks { view -> view.dispose() }

    private fun scaleImageAndLandmarks(newScale: Double) {
        canvas.scale(newScale)
        doForAllLandmarks { view -> view.scale = newScale }
    }

    private fun clearLandmarks() = children.removeIf { x -> x is Shape }

    private fun drawLoadingIndicator() = canvas.fill(Color.GREY)

    private fun doForAllLandmarks(delegate: (LandmarkView) -> Unit) =
        landmarksViews?.forEach { (layer, landmarkViews) ->
            landmarkViews.forEach { view -> delegate(view) }
        }

    private fun VisualizationFrame.hasPoints() =
        this.layers.filterIsInstance<Layer.PointLayer>().isNotEmpty()
}