package solve.scene.model

sealed class Landmark(val uid: Long, open val layerSettings: LayerSettings) {
    class Keypoint(uid: Long, override val layerSettings: LayerSettings.PointLayerSettings, val coordinate: Point) :
        Landmark(uid, layerSettings)

    class Line(
        uid: Long,
        override val layerSettings: LayerSettings.LineLayerSettings,
        val startCoordinate: Point,
        val finishCoordinate: Point
    ) : Landmark(uid, layerSettings)

    class Plane(uid: Long, override val layerSettings: LayerSettings.PlaneLayerSettings, val points: List<Point>) :
        Landmark(uid, layerSettings)
}