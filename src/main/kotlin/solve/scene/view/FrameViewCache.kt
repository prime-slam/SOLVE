package solve.scene.view

import solve.scene.model.VisualizationFrame
import solve.utils.structures.Size as DoubleSize

interface FrameViewStorage {
    fun store(frameView: FrameView)
}

class FrameViewCache(
    val size: DoubleSize,
    val canvasBufferDepth: Int,
    private val factory: (VisualizationFrame?, FrameViewParameters) -> FrameView
) : FrameViewStorage {
    private val storage = mutableSetOf<FrameView>()

    fun get(frame: VisualizationFrame?, frameViewParameters: FrameViewParameters) =
        storage.firstOrNull()?.also { frameView ->
            frameView.init(frame, frameViewParameters)
            storage.remove(frameView)
        } ?: factory(frame, frameViewParameters)

    override fun store(frameView: FrameView) {
        if (frameView.size != size) {
            throw IllegalArgumentException()
        }
        storage.add(frameView)
    }
}
