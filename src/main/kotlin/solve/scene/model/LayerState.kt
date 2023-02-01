package solve.scene.model

import tornadofx.toObservable

class LayerState(val name: String) {
    val selectedLandmarksUids = mutableSetOf<Long>().toObservable()
    val hoveredLandmarksUids = mutableSetOf<Long>().toObservable()
}