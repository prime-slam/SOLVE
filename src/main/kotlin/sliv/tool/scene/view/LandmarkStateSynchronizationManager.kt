package sliv.tool.scene.view

import tornadofx.*

class LandmarkStateSynchronizationManager {
    val selectedLandmarksUids = mutableSetOf<Long>().toObservable()
    val hoveredLandmarksUids = mutableSetOf<Long>().toObservable()
}