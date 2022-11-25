package sliv.tool.scene.view

import sliv.tool.common.Event

class LandmarkStateSynchronizationManager {
    val landmarkSelected = Event<LandmarkEventArgs>()
    val landmarkUnselected = Event<LandmarkEventArgs>()
    val landmarkMouseEntered = Event<LandmarkEventArgs>()
    val landmarkMouseExited = Event<LandmarkEventArgs>()

    private val selectedLandmarksUidsStorage = mutableSetOf<Long>()
    private val hoveredLandmarksUidsStorage = mutableSetOf<Long>()
    val selectedLandmarksUids
        get() = selectedLandmarksUidsStorage.toSet()

    val hoveredLandmarksUids
        get() = hoveredLandmarksUidsStorage.toSet()

    init {
        landmarkSelected += { e -> selectedLandmarksUidsStorage.add(e.uid) }
        landmarkUnselected += { e -> selectedLandmarksUidsStorage.remove(e.uid) }
        landmarkMouseEntered += { e -> hoveredLandmarksUidsStorage.add(e.uid) }
        landmarkMouseExited += { e -> hoveredLandmarksUidsStorage.remove(e.uid) }
    }
}