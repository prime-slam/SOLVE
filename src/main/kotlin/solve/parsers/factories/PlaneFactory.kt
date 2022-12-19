package solve.parsers.factories

import solve.parsers.structures.Plane as StoragePlane
import solve.parsers.structures.Plane
import solve.scene.model.Landmark.Plane as ScenePlane
import solve.scene.model.LayerSettings.PlaneLayerSettings

object PlaneFactory : LandmarkFactory<StoragePlane, ScenePlane, PlaneLayerSettings> {
    override fun buildLandmark(storageFormatLandmark: Plane, containingLayer: PlaneLayerSettings): ScenePlane =
        ScenePlane(storageFormatLandmark.uid, containingLayer, storageFormatLandmark.points)
}
