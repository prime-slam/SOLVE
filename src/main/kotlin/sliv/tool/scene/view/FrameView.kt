package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import javafx.scene.transform.Scale
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import sliv.tool.scene.model.Layer
import sliv.tool.scene.model.VisualizationFrame
import tornadofx.add
import tornadofx.onChange

class FrameView(
    private val width: Double,
    private val height: Double,
    private val scale: DoubleProperty,
    private val coroutineScope: CoroutineScope,
    private val eventManager: LandmarkStateSynchronizationManager,
    frame: VisualizationFrame?
) : Group() {
    //Frame data be loaded concurrently, so these fields should be volatile
    @Volatile
    private var image: Image? = null

    @Volatile
    private var timestamp: Long? = null

    @Volatile
    private var landmarksViews: Map<Layer, List<LandmarkView>>? = null

    private val imageCanvas = Canvas(width, height)
    private var currentJob: Job? = null

    private val isDataLoaded
        get() = landmarksViews != null && image != null && timestamp != null

    init {
        scale.onChange { newScale ->
            scaleImageAndLandmarks(newScale)
        }

        add(imageCanvas)
        setFrame(frame)
        scaleImageAndLandmarks(scale.value)
    }

    fun setFrame(frame: VisualizationFrame?) {
        currentJob?.cancel()
        disposeLandmarkViews()
        landmarksViews = null
        image = null
        timestamp = null

        clearLandmarks()
        clearImage()

        if (frame == null) {
            return
        }

        currentJob = coroutineScope.launch {
            reloadData(frame)
            withContext(Dispatchers.JavaFx) {
                draw()
            }
        }

        draw()
    }

    fun dispose() {
        disposeLandmarkViews()
    }

    private fun reloadData(frame: VisualizationFrame) {
        landmarksViews = frame.landmarks.mapValues {
            it.value.map { landmark -> LandmarkView.create(landmark, scale.value, frame.timestamp, eventManager) }
        }
        val frameImage = frame.image
        if (frameImage.height != height || frameImage.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }
        image = frameImage
        timestamp = frame.timestamp
    }

    private fun disposeLandmarkViews() = doForAllLandmarks { view -> view.dispose() }

    private fun draw() {
        if (!isDataLoaded) {
            drawLoadingIndicator()
            return
        }

        drawImage()
        doForAllLandmarks { view ->
            children.add(view.node)
        }
    }

    private fun scaleImageAndLandmarks(newScale: Double) {
        imageCanvas.transforms.clear()
        clearImage()

        if (newScale > 1) {
            imageCanvas.width = width
            imageCanvas.height = height
            val scaleTransform = Scale(newScale, newScale)
            imageCanvas.transforms.add(scaleTransform)
        } else {
            imageCanvas.width = width * newScale
            imageCanvas.height = height * newScale
        }

        drawImage()
        doForAllLandmarks { view -> view.scale = newScale }
    }

    private fun drawImage() {
        imageCanvas.graphicsContext2D.drawImage(image, 0.0, 0.0, imageCanvas.width, imageCanvas.height)
    }

    private fun clearImage() {
        imageCanvas.graphicsContext2D.clearRect(0.0, 0.0, imageCanvas.width, imageCanvas.height)
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