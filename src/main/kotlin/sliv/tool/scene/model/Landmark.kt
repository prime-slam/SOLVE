package sliv.tool.scene.model

sealed class Landmark(val uid: Long, layer: Layer) {
    class Keypoint(uid: Long, val layer: Layer.PointLayer, val coordinate: Point) : Landmark(uid, layer)
    class Line(uid: Long, val layer: Layer.LineLayer, val startCoordinate: Point, val finishCoordinate: Point) :
        Landmark(uid, layer)

    class Plane(uid: Long, val layer: Layer.PlaneLayer, val points: Array<Point>) : Landmark(uid, layer)
}