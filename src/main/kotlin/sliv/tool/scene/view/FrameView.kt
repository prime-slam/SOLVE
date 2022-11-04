package sliv.tool.scene.view

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.canvas.*
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import sliv.tool.scene.model.*
import tornadofx.*

class FrameView(width: Double, height: Double, frame: VisualizationFrame) : Group() {
    private var landmarksViews: Map<Layer, List<LandmarkView>>? = null
    private val canvas = Canvas(width, height)

    init {
        setFrame(frame)
        canvas.onMouseMoved = EventHandler { event: MouseEvent ->
            onMouseMoved(event)
        }
        add(canvas)
    }

    fun setFrame(frame: VisualizationFrame) {
        //TODO: warn if image size doesn't equal to the frame size
        landmarksViews = frame.landmarks.mapValues {
            it.value.map { landmark -> LandmarkView.create(landmark) }
        }
        draw()
    }

    private fun draw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
//        gc.drawImage(frame.image, 0.0, 0.0)
        drawFakeImage() //TODO: real images can be used only with data virtualization
        doForAllEnabledLandmarks { view -> view.draw(gc) }
    }

    private fun drawFakeImage() {
        val gc = canvas.graphicsContext2D
        gc.fill = Color.GREY
        gc.fillRect(0.0, 0.0, canvas.width, canvas.height)
    }

    private fun doForAllEnabledLandmarks(delegate: (LandmarkView) -> Unit) {
        landmarksViews?.forEach { (layer, landmarkViews) ->
            if (!layer.enabled) {
                return
            }
            landmarkViews.forEach { view -> delegate(view) }
        }
    }

    private fun onMouseMoved(event: MouseEvent) {
        var stateChanged = false
        doForAllEnabledLandmarks { view ->
            val prevState = view.state
            view.updateIsHovered(event)
            stateChanged = stateChanged || view.state != prevState
        }

        if (stateChanged) {
            draw()
        }
    }
}