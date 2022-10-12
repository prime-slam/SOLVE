package sliv.tool.model.visualization

//Scene controls virtualization, so it is not needed to supply it with all frames from the start.
//View demands new frames from the controller, controller asks this model about it
//getFrame function implemented in the controller, which knows about .
class Scene(val getFrame: (Int) -> VisualizationFrame?, val framesCount: Int, val layers: List<Layer>) {
    private val loadedFrames: Map<Int, VisualizationFrame> = HashMap<Int, VisualizationFrame>()
    private val viewportBorders = Pair(Pair(0, 10), Pair(0, 20))
}