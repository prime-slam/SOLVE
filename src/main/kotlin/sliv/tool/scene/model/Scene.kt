package sliv.tool.scene.model

import com.sun.javafx.geom.Rectangle

//Scene controls virtualization, so it is not needed to supply it with all frames from the start.
//View demands new frames from the controller, controller asks this model about it
//getFrame function implemented in the controller, which knows about .
class Scene(val getFrame: (Int) -> VisualizationFrame?, val framesCount: Int, val layers: List<Layer>) {
    private val loadedFrames: Map<Int, VisualizationFrame> = HashMap<Int, VisualizationFrame>()
    private val viewport = Rectangle(0, 0, 20, 20)
}