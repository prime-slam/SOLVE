package solve.scene.model

import tornadofx.toObservable

// Contains landmarks settings which shouldn't be reused when scene is recreated
class LayerState(val name: String) {
    val selectedLandmarksUids = mutableSetOf<Long>().toObservable()
    val hoveredLandmarksUids = mutableSetOf<Long>().toObservable()
}