package solve.scene.view

import solve.scene.model.VisualizationFrame

object DelayedFrameUpdatesManager {
    private val delayedUpdates = mutableMapOf<FrameView, VisualizationFrame?>()

    var shouldDelay = false
        private set

    fun doLockedAction(action: () -> Unit) {
        shouldDelay = true
        action()
        shouldDelay = false
        delayedUpdates.forEach { (view, newFrame) ->
            view.setFrame(newFrame)
        }
        delayedUpdates.clear()
    }

    fun delayUpdate(view: FrameView, newFrame: VisualizationFrame?) {
        delayedUpdates[view] = newFrame
    }
}