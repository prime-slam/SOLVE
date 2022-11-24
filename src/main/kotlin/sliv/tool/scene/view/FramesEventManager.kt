package sliv.tool.scene.view

import sliv.tool.common.*

object FramesEventManager {
    val LandmarkSelected = Event<LandmarkEventArgs>()
    val LandmarkUnselected = Event<LandmarkEventArgs>()

    fun resetListeners() {
        LandmarkSelected.clear()
        LandmarkUnselected.clear()
    }
}