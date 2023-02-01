package solve.scene.view

import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.scene.transform.Scale
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
    private val imageCanvas = Canvas(width, height)
    private var currentJob: Job? = null

    private val scaleChangedListener = InvalidationListener { _ -> scaleImageAndLandmarks(scale.value) }

    init {
        scale.addListener(WeakInvalidationListener(scaleChangedListener))

        add(imageCanvas)
        setFrame(frame)
        scaleImageAndLandmarks(scale.value)

        setOnMouseClicked {
            if (it.button != MouseButton.PRIMARY) {
                return@setOnMouseClicked
            }
            val clickedFrame = currentFrame ?: return@setOnMouseClicked
            val associationParameters =
                AssociationsManager.AssociationParameters(clickedFrame, getKeypoints(clickedFrame))
            associationsManager.chooseFrame(associationParameters)
        }

        contextmenu {
            item("Associate keypoints").action {
                val clickedFrame = currentFrame ?: return@action
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
        clearImage()

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
        landmarksViews = landmarks.mapValues {
            it.value.map { landmark ->
                LandmarkView.create(landmark, scale.value)
            }
        }

        drawnImage = image
        if (image.height != height || image.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }

        drawImage(image)
        doForAllLandmarks { view -> children.add(view.node) }
    }

    private fun disposeLandmarkViews() = doForAllLandmarks { view -> view.dispose() }

    private fun scaleImageAndLandmarks(newScale: Double) {
        imageCanvas.transforms.clear()
        if (drawnImage != null) {
            clearImage()
        }

        if (newScale > 1) {
            imageCanvas.width = width
            imageCanvas.height = height
            val scaleTransform = Scale(newScale, newScale)
            imageCanvas.transforms.add(scaleTransform)
        } else {
            imageCanvas.width = width * newScale
            imageCanvas.height = height * newScale
        }

        drawImage(drawnImage)
        doForAllLandmarks { view -> view.scale = newScale }
    }

    private fun drawImage(image: Image?) {
        if (image == null) {
            return
        }
        imageCanvas.graphicsContext2D.drawImage(image, 0.0, 0.0, imageCanvas.width, imageCanvas.height)
    }

    private fun clearImage() {
        imageCanvas.graphicsContext2D.clearRect(0.0, 0.0, Int.MAX_VALUE.toDouble(), Int.MAX_VALUE.toDouble())
    }

    private fun clearLandmarks() {
        children.removeIf { x -> x is Shape }
    }

    private fun drawLoadingIndicator() {
        imageCanvas.graphicsContext2D.fill = Color.GREY
        imageCanvas.graphicsContext2D.fillRect(0.0, 0.0, imageCanvas.width, imageCanvas.height)
    }

    private fun doForAllLandmarks(delegate: (LandmarkView) -> Unit) =
        landmarksViews?.forEach { (layer, landmarkViews) ->
            landmarkViews.forEach { view -> delegate(view) }
        }
}