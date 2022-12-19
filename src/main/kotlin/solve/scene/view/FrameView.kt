package solve.scene.view

import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
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

    init {
        scale.onChange { newScale ->
            scaleImageAndLandmarks(newScale)
        }

        add(imageCanvas)
        setFrame(frame)
        scaleImageAndLandmarks(scale.value)

        setOnMouseClicked {
            if (!it.isPrimaryButtonDown) {
                return@setOnMouseClicked
            }
            val clickedFrame = currentFrame ?: return@setOnMouseClicked
            associationsManager.chooseFrame(clickedFrame)
        }

        contextmenu {
            item("Associate keypoints").action {
                val clickedFrame = currentFrame ?: return@action
                associationsManager.initAssociation(clickedFrame, clickedFrame.landmarks.keys.first()) // TODO
            }
            item("Clear association").action {
                val clickedFrame = currentFrame ?: return@action
                associationsManager.clearAssociation(clickedFrame, clickedFrame.landmarks.keys.first()) // TODO
            }
        }
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
            val landmarkData = frame.landmarks

            if (!isActive) return@launch
            val image = frame.image

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

    private fun draw(landmarksData: Map<Layer, List<Landmark>>, image: Image) {
        landmarksViews = landmarksData.mapValues {
            it.value.map { landmark -> LandmarkView.create(landmark, scale.value) }
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