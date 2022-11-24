package sliv.tool.scene.view

import sliv.tool.common.*

class FramesEventManager {
    val landmarkSelected = Event<LandmarkEventArgs>()
    val landmarkUnselected = Event<LandmarkEventArgs>()
}