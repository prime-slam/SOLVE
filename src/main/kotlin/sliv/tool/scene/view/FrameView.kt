package sliv.tool.scene.view

import javafx.event.EventHandler
import javafx.scene.canvas.*
import javafx.scene.input.MouseEvent
import sliv.tool.scene.model.*
import tornadofx.*

class FrameView(width: Double, height: Double, private val frame: VisualizationFrame) : Fragment() {
    private val canvas = Canvas(width, height)
    private var landmarksViews = buildMap(frame.landmarks.size) {
        frame.landmarks.forEach { layer -> run {
            put(layer.key, layer.value.map { landmark -> LandmarkView.create(landmark) })
        }}
    }

    override val root = group {
        draw()
        canvas.onMouseMoved = EventHandler { event: MouseEvent ->
            onMouseMoved(event)
        }
        add(canvas)
    }

    private fun draw() {
        val gc = canvas.graphicsContext2D
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
        gc.drawImage(frame.image, 0.0, 0.0)
        doForAllEnabledLandmarks { view -> view.draw(gc) }
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

    private fun doForAllEnabledLandmarks(delegate: (LandmarkView) -> Unit) {
        landmarksViews.forEach {
            if (!it.key.enabled) {
                return
            }
            it.value.forEach { view -> delegate(view) }
        }
    }
}