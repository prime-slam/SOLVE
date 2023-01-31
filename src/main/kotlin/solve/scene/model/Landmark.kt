package solve.scene.model

sealed class Landmark(val uid: Long, open val layerSettings: LayerSettings, val layerState: LayerState) {
    class Keypoint(
        uid: Long,
        override val layerSettings: LayerSettings.PointLayerSettings,
        layerState: LayerState,
        val coordinate: Point
    ) : Landmark(uid, layerSettings, layerState)

    class Line(
        uid: Long,
        override val layerSettings: LayerSettings.LineLayerSettings,
        layerState: LayerState,
        val startCoordinate: Point,
        val finishCoordinate: Point
    ) : Landmark(uid, layerSettings, layerState)

    class Plane(
        uid: Long,
        override val layerSettings: LayerSettings.PlaneLayerSettings,
        layerState: LayerState,
        val points: List<Point>
    ) : Landmark(uid, layerSettings, layerState)
}