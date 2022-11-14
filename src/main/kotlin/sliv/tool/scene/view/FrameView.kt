package sliv.tool.scene.view

import javafx.beans.property.DoubleProperty
import javafx.scene.Group
import javafx.scene.canvas.*
import javafx.scene.image.Image
import javafx.scene.paint.Color
import kotlinx.coroutines.*
import sliv.tool.scene.model.*
import tornadofx.*

class FrameView(
    private val width: Double, private val height: Double, private val scale: DoubleProperty, frame: VisualizationFrame
) : Group() {
    private var image: Image? = null
    private var landmarksViews: Map<Layer, List<LandmarkView>>? = null
    private val canvas = Canvas(width * scale.value, height * scale.value)

    init {
        scale.onChange { newScale ->
            canvas.height = height * newScale
            canvas.width = width * newScale
            draw()
        }

        setFrame(frame)
        canvas.setOnMouseMoved { event ->
            onMouseMoved(event.x, event.y)
        }
        canvas.setOnMouseExited {
            onMouseMoved(-1.0, -1.0)
        }
        add(canvas)
    }

    fun setFrame(frame: VisualizationFrame) {
        landmarksViews = null
        image = null

        GlobalScope.launch(Dispatchers.Default) {
            reloadData(frame)
            delay(2000)
            draw()
            println("Drawn from ${Thread.currentThread().name}")
        }
        println("Drawn from ${Thread.currentThread().name}")
        draw()
    }

    private fun reloadData(frame: VisualizationFrame) {
        landmarksViews = frame.landmarks.mapValues {
            it.value.map { landmark -> LandmarkView.create(landmark) }
        }
        image = frame.image
        if(image!!.height != height || image!!.width != width) {
            println("Image size doesn't equal to the frame size") //TODO: warn user
        }
    }

    private fun draw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        if(!isDataLoaded) {
            drawPlaceHolder()
            return
        }

        gc.drawImage(image, 0.0, 0.0, canvas.width, canvas.height)
        doForAllEnabledLandmarks { view -> view.draw(gc, scale.value) }
    }

    private val isDataLoaded
        get() = landmarksViews != null && image != null

    private fun drawPlaceHolder() {
        val gc = canvas.graphicsContext2D
        gc.fill = Color.GREY
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)
        gc.fill = Color.RED
        gc.fillText("Loading...", canvas.width / 2, canvas.height /2)
    }

    private fun doForAllEnabledLandmarks(delegate: (LandmarkView) -> Unit) {
        landmarksViews?.forEach { (layer, landmarkViews) ->
            if (!layer.enabled) {
                return
            }
            landmarkViews.forEach { view -> delegate(view) }
        }
    }

    private fun onMouseMoved(x: Double, y: Double) {
        var stateChanged = false
        doForAllEnabledLandmarks { view ->
            val prevState = view.state
            view.updateIsHovered(x, y, scale.value)
            stateChanged = stateChanged || view.state != prevState
        }

        if (stateChanged) {
            draw()
        }
    }
}