package sliv.tool.parsers.factories

import sliv.tool.parsers.structures.Plane as StoragePlane
import sliv.tool.parsers.structures.Plane
import sliv.tool.scene.model.Landmark.Plane as ScenePlane
import sliv.tool.scene.model.Layer.PlaneLayer

class PlaneFactory(private val rgbColorToUIDMap: Map<Int, Long>) :
    LandmarkFactory<StoragePlane, ScenePlane, PlaneLayer> {
    override fun buildLandmark(storageFormatLandmark: Plane, containingLayer: PlaneLayer): ScenePlane {
        var uid = rgbColorToUIDMap[storageFormatLandmark.rgbColor]
        if (uid == null) {
            println("No uid matches this color!")
            uid = -1
        }
        val points = storageFormatLandmark.points.toTypedArray()

        return ScenePlane(uid, containingLayer, points)
    }
}
