package solve.scene.view

import solve.scene.model.VisualizationFrame

interface FrameViewStorage {
    fun store(frameView: FrameView)
}

class FrameViewCache(private val factory: (VisualizationFrame?) -> FrameView): FrameViewStorage {
    private val storage = mutableSetOf<FrameView>()

    fun get(frame: VisualizationFrame?) = storage.firstOrNull()?.also { frameView ->
        frameView.init()
        storage.remove(frameView)
    } ?: factory(frame)

    override fun store(frameView: FrameView) {
        storage.add(frameView)
    }
}