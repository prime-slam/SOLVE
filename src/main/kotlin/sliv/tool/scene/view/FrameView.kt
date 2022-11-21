package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
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
    frame: VisualizationFrame
) : Group() {
    //Frame data be loaded concurrently, so these fields should be volatile
    @Volatile
    private var image: Image? = null

    @Volatile
    private var landmarksViews: Map<Layer, List<LandmarkView>>? = null

    private val imageView = ImageView()
    private var currentJob: Job? = null

    init {
        println("create frame")
        scale.onChange { newScale ->
            imageView.transforms.clear()
            if (newScale > 1) {
                imageView.fitWidth = width
                imageView.fitHeight = height
                val scaleTransform = Scale(newScale, newScale)
                imageView.transforms.add(scaleTransform)
            } else {
                imageView.fitWidth = width * newScale
                imageView.fitHeight = height * newScale
            }

            doForAllLandmarks { view -> view.scale = newScale }
        }

        setFrame(frame)
        add(imageView)
    }

    fun setFrame(frame: VisualizationFrame) {
        currentJob?.cancel()
        landmarksViews = null
        image = null

        currentJob = coroutineScope.launch {
            reloadData(frame)
            withContext(Dispatchers.JavaFx) {
                draw()
            }
        }

        draw()
    }

    private fun reloadData(frame: VisualizationFrame) {
        landmarksViews = frame.landmarks.mapValues {
            it.value.map { landmark -> LandmarkView.create(landmark, scale.value) }
        }
        val frameImage = frame.image
        if (frameImage.height != height || frameImage.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }
        image = frameImage
    }

    private fun draw() {
        children.clear()
        //doForAllLandmarks { view -> children.remove(view.shape) }

        if (!isDataLoaded) {
            drawLoadingIndicator()
            return
        }

        imageView.image = image
        children.add(imageView)
        doForAllLandmarks { view -> children.add(view.shape) }
    }

    private val isDataLoaded
        get() = landmarksViews != null && image != null

    private fun drawLoadingIndicator() {
        val rect = Rectangle(width, height)
        rect.fill = Color.GREY
        rect.transforms.add(Scale(scale.value, scale.value))
        children.add(rect)
    }

    private fun doForAllLandmarks(delegate: (LandmarkView) -> Unit) =
        landmarksViews?.forEach { (layer, landmarkViews) ->
            landmarkViews.forEach { view -> delegate(view) }
        }
}