package sliv.tool.parsers.factories

import sliv.tool.parsers.structures.Plane as StoragePlane
import sliv.tool.parsers.structures.Plane
import sliv.tool.scene.model.Landmark.Plane as ScenePlane
import sliv.tool.scene.model.Layer.PlaneLayer

object PlaneFactory : LandmarkFactory<StoragePlane, ScenePlane, PlaneLayer> {
    override fun buildLandmark(storageFormatLandmark: Plane, containingLayer: PlaneLayer): ScenePlane =
        ScenePlane(storageFormatLandmark.uid, containingLayer, storageFormatLandmark.points.toTypedArray())
}
