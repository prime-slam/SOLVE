package solve.scene.view

import solve.scene.model.VisualizationFrame
import solve.utils.structures.Size as DoubleSize

interface FrameViewStorage {
    fun store(frameView: FrameView)
}

class FrameViewCache(val size: DoubleSize, private val factory: (VisualizationFrame?) -> FrameView): FrameViewStorage {
    private val storage = mutableSetOf<FrameView>()

    fun get(frame: VisualizationFrame?) = storage.firstOrNull()?.also { frameView ->
        frameView.init()
        storage.remove(frameView)
    } ?: factory(frame)

    override fun store(frameView: FrameView) {
        if (frameView.size != size) {
            throw IllegalArgumentException()
        }
        storage.add(frameView)
    }
}