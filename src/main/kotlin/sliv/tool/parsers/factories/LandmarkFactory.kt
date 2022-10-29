package sliv.tool.parsers.factories

import sliv.tool.scene.model.Landmark
import sliv.tool.scene.model.Layer

// An interface that defines a method for creating a scene landmark by scene layer and a data class corresponding to the
// data storage format.
// Accepts T parameter as a data class corresponding to the data storage format.
// Accepts S parameter as a scene landmark class.
interface LandmarkFactory<T, S : Landmark> {
    fun buildLandmark(storageFormatLandmark: T, containingLayer: Layer): S
}
