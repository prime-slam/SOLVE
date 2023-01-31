package solve.parsers.factories

import solve.scene.model.Landmark
import solve.scene.model.LayerSettings
import solve.scene.model.LayerState

// An interface that defines a method for creating a scene landmark by scene layer and a data class corresponding to the
// data storage format.
// Accepts T parameter as a data class corresponding to the data storage format.
// Accepts S parameter as a scene landmark class.
// Accepts C parameter as a corresponding layer.
interface LandmarkFactory<T, S : Landmark, C : LayerSettings> {
    fun buildLandmark(storageFormatLandmark: T, containingLayerSettings: C, containingLayerState: LayerState): S
}
