package solve.scene.model

sealed class Landmark(val uid: Long, open val layer: Layer) {
    class Keypoint(uid: Long, override val layer: Layer.PointLayer, val coordinate: Point) : Landmark(uid, layer)
    class Line(uid: Long, override val layer: Layer.LineLayer, val startCoordinate: Point, val finishCoordinate: Point) :
        Landmark(uid, layer)

    class Plane(uid: Long, override val layer: Layer.PlaneLayer, val points: List<Point>) : Landmark(uid, layer)
}