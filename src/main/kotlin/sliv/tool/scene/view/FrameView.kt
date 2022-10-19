package sliv.tool.scene.view

import javafx.event.EventHandler
import javafx.scene.canvas.*
import javafx.scene.input.MouseEvent
import sliv.tool.scene.model.*
import tornadofx.*

class FrameView(width: Double, height: Double, private val frame: VisualizationFrame) : Fragment() {
    private val canvas = Canvas(width, height)
    private val landmarksViews = HashMap<Layer, List<LandmarkView>>()

    init {
        frame.landmarks.forEach { layer ->
            landmarksViews[layer.key] =
                layer.value.map { landmark -> LandmarkView.create(canvas.graphicsContext2D, landmark) }
        }
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
        doForAllEnabledLandmarks { view -> view.draw() }
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
        for (layer in landmarksViews) {
            if (!layer.key.enabled) {
                continue
            }
            layer.value.forEach { view -> delegate(view) }
        }
    }
}