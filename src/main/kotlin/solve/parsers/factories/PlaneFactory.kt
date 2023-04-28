package solve.parsers.factories

import solve.parsers.structures.Plane
import solve.scene.model.LayerSettings.PlaneLayerSettings
import solve.scene.model.LayerState
import solve.parsers.structures.Plane as StoragePlane
import solve.scene.model.Landmark.Plane as ScenePlane

object PlaneFactory : LandmarkFactory<StoragePlane, ScenePlane, PlaneLayerSettings> {
    override fun buildLandmark(
        storageFormatLandmark: Plane,
        containingLayerSettings: PlaneLayerSettings,
        containingLayerState: LayerState
    ): ScenePlane = ScenePlane(
        storageFormatLandmark.uid,
        containingLayerSettings,
        containingLayerState,
        storageFormatLandmark.points
    )
}
