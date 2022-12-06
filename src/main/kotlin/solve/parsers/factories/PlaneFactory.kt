package solve.parsers.factories

import solve.parsers.structures.Plane as StoragePlane
import solve.parsers.structures.Plane
import solve.scene.model.Landmark.Plane as ScenePlane
import solve.scene.model.Layer.PlaneLayer

object PlaneFactory : LandmarkFactory<StoragePlane, ScenePlane, PlaneLayer> {
    override fun buildLandmark(storageFormatLandmark: Plane, containingLayer: PlaneLayer): ScenePlane =
        ScenePlane(storageFormatLandmark.uid, containingLayer, storageFormatLandmark.points)
}
