package sliv.tool.scene.view

import sliv.tool.common.*

data class LandmarkEventArgs(val uid: Long, val frameTimestamp: Long)

object FramesEventManager {
    val LandmarkSelected = Event<LandmarkEventArgs>()
    val LandmarkUnselected = Event<LandmarkEventArgs>()

    fun resetListeners() {
        LandmarkSelected.clear()
        LandmarkUnselected.clear()
    }
}